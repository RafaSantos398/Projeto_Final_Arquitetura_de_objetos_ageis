package com.berxy.projeto.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(String nome, String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyInUseException(normalizedEmail);
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = new User(nome.trim(), normalizedEmail, passwordHash);

        return userRepository.save(user);
    }

    // Soft-delete: usuários desativados se comportam como inexistentes, exceto para reativação.
    // O e-mail deles continua reservado, senão a reativação poderia gerar duplicidade.
    public User findById(Long id) {
        return userRepository.findById(id)
                .filter(User::isActive)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);

        return userRepository.findByEmail(normalizedEmail)
                .filter(User::isActive)
                .orElseThrow(() -> new UserNotFoundException(normalizedEmail));
    }

    public List<User> findAll() {
        return userRepository.findAllByActiveTrue();
    }

    // Busca no banco e devolve UserDetails, como nas Aulas 09 e 13.
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) {
        User usuario = userRepository.findByEmail(normalizeEmail(email))
                .filter(User::isActive)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("Credenciais inválidas."));
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getEmail()).password(usuario.getPasswordHash())
                .roles(usuario.getRole().name()).build();
    }

    @Transactional
    public User update(Long id, String nome, String email) {
        User user = findById(id);
        String normalizedEmail = normalizeEmail(email);

        boolean emailChanged = !user.getEmail().equals(normalizedEmail);
        if (emailChanged && userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyInUseException(normalizedEmail);
        }

        user.setNome(nome.trim());
        user.setEmail(normalizedEmail);

        // Força o UPDATE agora para que uma violação do unique (corrida) seja traduzida aqui
        userRepository.flush();

        return user;
    }

    @Transactional
    public void changePassword(Long id, String currentPassword, String newPassword) {
        User user = findById(id);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void deactivate(Long id) {
        findIncludingInactive(id).deactivate();
    }

    @Transactional
    public void activate(Long id) {
        findIncludingInactive(id).activate();
    }

    private User findIncludingInactive(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
