package com.berxy.projeto.perfilIntercambista;
import com.berxy.projeto.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PerfilService {
    private final PerfilRepository repository;
    private final UserService users;
    public PerfilService(PerfilRepository repository, UserService users) {
        this.repository = repository;
        this.users = users;
    }
    public PerfilResponse consultar(Long usuarioId) {
        users.findById(usuarioId);
        return PerfilResponse.from(buscar(usuarioId));
    }
    @Transactional
    public PerfilResponse cadastrar(Long usuarioId, PerfilRequest dados) {
        var user = users.findById(usuarioId);
        if (repository.existsById(usuarioId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuario ja possui perfil");
        return PerfilResponse.from(repository.saveAndFlush(new PerfilINtercambista(user, dados)));
    }
    @Transactional
    public PerfilResponse atualizar(Long usuarioId, PerfilRequest dados) {
        users.findById(usuarioId);
        var perfil = buscar(usuarioId);
        perfil.atualizar(dados);
        return PerfilResponse.from(perfil);
    }
    private PerfilINtercambista buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil nao encontrado"));
    }
}
