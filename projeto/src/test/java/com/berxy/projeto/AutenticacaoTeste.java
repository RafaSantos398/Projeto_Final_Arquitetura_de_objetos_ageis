package com.berxy.projeto;

import com.berxy.projeto.security.JwtService;
import com.berxy.projeto.user.Role;
import com.berxy.projeto.user.User;
import com.berxy.projeto.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public final class AutenticacaoTeste {
    private AutenticacaoTeste() { }

    public static void exigirBancoIsolado(DataSource dataSource) throws Exception {
        try (var connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            boolean h2Memoria = url.startsWith("jdbc:h2:mem:");
            boolean postgresIsolado = url.startsWith("jdbc:postgresql:")
                    && connection.getSchema() != null
                    && connection.getSchema().matches("codex_test_[a-f0-9]{12}");
            if (!h2Memoria && !postgresIsolado) {
                throw new IllegalStateException("Use H2 em memória ou um esquema PostgreSQL temporário codex_test_.");
            }
        }
    }

    // Os testes de domínio usam um administrador real e passam pelo filtro JWT.
    // AuthControllerTest exercita separadamente o login real por senha.
    public static MockMvc comoAdministrador(WebApplicationContext context) throws Exception {
        exigirBancoIsolado(context.getBean(DataSource.class));
        var usuarios = context.getBean(UserRepository.class);
        var encoder = context.getBean(PasswordEncoder.class);
        User admin = new User("Admin teste", UUID.randomUUID() + "@admin.test", encoder.encode("senha-admin-teste"));
        admin.setRole(Role.ADMIN);
        usuarios.saveAndFlush(admin);
        String token = context.getBean(JwtService.class).generateToken(admin.getEmail());
        return MockMvcBuilders.webAppContextSetup(context).apply(springSecurity())
                .defaultRequest(get("/").header("Authorization", "Bearer " + token)).build();
    }
}
