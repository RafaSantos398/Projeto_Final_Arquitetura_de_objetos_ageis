package com.berxy.projeto.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired org.springframework.web.context.WebApplicationContext context;

    @org.junit.jupiter.api.BeforeEach
    void autenticarAdministrador() throws Exception {
        mockMvc = com.berxy.projeto.AutenticacaoTeste.comoAdministrador(context);
    }

    private static final String PASSWORD = "senha-forte";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Cadastro

    @Test
    void createNormalizesDataAndDoesNotExposePassword() throws Exception {
        postUser("  Ana  ", "ANA@Example.com", PASSWORD)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/users/" + idOf("ana@example.com"))))
                .andExpect(jsonPath("$.nome").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        User saved = userRepository.findByEmail("ana@example.com").orElseThrow();
        assertTrue(passwordEncoder.matches(PASSWORD, saved.getPasswordHash()));
    }

    @Test
    void createRejectsInvalidFields() throws Exception {
        postUser(" ", "nao-e-email", "curta")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nome").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void createRejectsDuplicatedEmailIgnoringCase() throws Exception {
        userService.create("Ana", "ana@example.com", PASSWORD);

        postUser("Outra Ana", "ANA@example.com", PASSWORD)
                .andExpect(status().isConflict());
    }

    @Test
    void createKeepsEmailOfDeactivatedUserReserved() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);
        userService.deactivate(user.getId());

        postUser("Outra Ana", "ana@example.com", PASSWORD)
                .andExpect(status().isConflict());
    }

    @Test
    void createAcceptsPasswordAtTheBcryptLimit() throws Exception {
        postUser("Ana", "ana@example.com", "a".repeat(72))
                .andExpect(status().isCreated());
    }

    @Test
    void createRejectsPasswordOverTheBcryptLimitInBytes() throws Exception {
        // 72 caracteres, mas 144 bytes em UTF-8
        postUser("Ana", "ana@example.com", "á".repeat(72))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
    }

    // Consulta

    @Test
    void findByIdReturnsUser() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@example.com"));
    }

    @Test
    void findByIdReturnsNotFoundForUnknownUser() throws Exception {
        mockMvc.perform(get("/users/{id}", 999_999))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdRejectsNonNumericId() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deactivatedUserIsHiddenFromQueries() throws Exception {
        User active = userService.create("Ana", "ana@example.com", PASSWORD);
        User inactive = userService.create("Bia", "bia@example.com", PASSWORD);
        userService.deactivate(inactive.getId());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.id == " + active.getId() + ")]").isNotEmpty())
                .andExpect(jsonPath("$[?(@.id == " + inactive.getId() + ")]").isEmpty());

        mockMvc.perform(get("/users/{id}", inactive.getId()))
                .andExpect(status().isNotFound());
    }

    // Atualização

    @Test
    void updateChangesNameAndEmail() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);

        putUser(user.getId(), " Ana Maria ", "ANAMARIA@example.com")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Maria"))
                .andExpect(jsonPath("$.email").value("anamaria@example.com"));
    }

    @Test
    void updateAcceptsOwnEmail() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);

        putUser(user.getId(), "Ana Maria", "ana@example.com")
                .andExpect(status().isOk());
    }

    @Test
    void updateRejectsEmailOfAnotherUser() throws Exception {
        userService.create("Ana", "ana@example.com", PASSWORD);
        User other = userService.create("Bia", "bia@example.com", PASSWORD);

        putUser(other.getId(), "Bia", "ana@example.com")
                .andExpect(status().isConflict());
    }

    @Test
    void updateReturnsNotFoundForDeactivatedUser() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);
        userService.deactivate(user.getId());

        putUser(user.getId(), "Ana", "ana@example.com")
                .andExpect(status().isNotFound());
    }

    // Senha

    @Test
    void changePasswordReplacesHash() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);

        patchPassword(user.getId(), PASSWORD, "nova-senha-forte")
                .andExpect(status().isNoContent());

        assertTrue(passwordEncoder.matches("nova-senha-forte", user.getPasswordHash()));
        assertFalse(passwordEncoder.matches(PASSWORD, user.getPasswordHash()));
    }

    @Test
    void changePasswordRejectsWrongCurrentPassword() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);

        patchPassword(user.getId(), "senha-errada", "nova-senha-forte")
                .andExpect(status().isBadRequest());

        assertTrue(passwordEncoder.matches(PASSWORD, user.getPasswordHash()));
    }

    @Test
    void changePasswordRejectsNewPasswordOverTheBcryptLimit() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);

        patchPassword(user.getId(), PASSWORD, "á".repeat(72))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.newPassword").exists());
    }

    @Test
    void changePasswordReturnsNotFoundForDeactivatedUser() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);
        userService.deactivate(user.getId());

        patchPassword(user.getId(), PASSWORD, "nova-senha-forte")
                .andExpect(status().isNotFound());
    }

    // Ativação

    @Test
    void deactivateCanBeRepeatedAndActivateRestoresUser() throws Exception {
        User user = userService.create("Ana", "ana@example.com", PASSWORD);

        mockMvc.perform(delete("/users/{id}", user.getId())).andExpect(status().isNoContent());
        mockMvc.perform(delete("/users/{id}", user.getId())).andExpect(status().isNoContent());

        mockMvc.perform(patch("/users/{id}/activate", user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void activateAndDeactivateReturnNotFoundForUnknownUser() throws Exception {
        mockMvc.perform(patch("/users/{id}/activate", 999_999)).andExpect(status().isNotFound());
        mockMvc.perform(delete("/users/{id}", 999_999)).andExpect(status().isNotFound());
    }

    private Long idOf(String email) {
        return userRepository.findByEmail(email).orElseThrow().getId();
    }

    private ResultActions postUser(String nome, String email, String password) throws Exception {
        return mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome": "%s", "email": "%s", "password": "%s"}
                        """.formatted(nome, email, password)));
    }

    private ResultActions putUser(Long id, String nome, String email) throws Exception {
        return mockMvc.perform(put("/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome": "%s", "email": "%s"}
                        """.formatted(nome, email)));
    }

    private ResultActions patchPassword(Long id, String currentPassword, String newPassword) throws Exception {
        return mockMvc.perform(patch("/users/{id}/password", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"currentPassword": "%s", "newPassword": "%s"}
                        """.formatted(currentPassword, newPassword)));
    }
}
