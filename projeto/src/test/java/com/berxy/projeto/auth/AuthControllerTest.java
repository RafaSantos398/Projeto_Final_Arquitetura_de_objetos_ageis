package com.berxy.projeto.auth;

import com.berxy.projeto.AutenticacaoTeste;
import com.berxy.projeto.PlanoIntercambio.*;
import com.berxy.projeto.user.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    @Autowired MockMvc mvc;
    @Autowired UserService users;
    @Autowired UserRepository usuarios;
    @Autowired PlanoIntercambioService planos;
    @Autowired PlanoIntercambioRepository planoRepository;
    @Autowired PasswordEncoder encoder;
    @Autowired DataSource dataSource;
    @Value("${jwt.secret}") String segredo;
    private static final String SENHA = "senha-segura-123";

    @BeforeEach
    void exigirBancoIsolado() throws Exception { AutenticacaoTeste.exigirBancoIsolado(dataSource); }

    @Test
    void frontendPermitidoFazPreflightERecebeCabecalhosInclusiveNoErro401() throws Exception {
        mvc.perform(options("/planos-intercambio")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        mvc.perform(get("/auth/me").header("Origin", "http://localhost:5173"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        User ana = users.create("Ana", "ana@example.com", SENHA);
        mvc.perform(get("/auth/me").header("Origin", "http://localhost:5173")
                        .header("Authorization", bearer(login(ana))))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void origemNaoConfiguradaNaoPodeAcessarAApi() throws Exception {
        mvc.perform(options("/auth/login").header("Origin", "https://desconhecido.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
        User ana = users.create("Ana", "ana@example.com", SENHA);
        mvc.perform(get("/auth/me").header("Origin", "https://desconhecido.example")
                        .header("Authorization", bearer(login(ana))))
                .andExpect(status().isForbidden());
    }

    @Test
    void cadastroPublicoValidaCriptografaENaoPermiteEscolherAdmin() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome":" Ana ","email":"ANA@example.com","password":"senha-segura-123","role":"ADMIN"}
                """))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.usuario.nome").value("Ana"))
                .andExpect(jsonPath("$.usuario.email").value("ana@example.com"))
                .andExpect(jsonPath("$.usuario.role").value("USER"))
                .andExpect(jsonPath("$.usuario.passwordHash").doesNotExist());
        assertTrue(encoder.matches(SENHA, usuarios.findByEmail("ana@example.com").orElseThrow().getPasswordHash()));
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"email\":\"ana@example.com\",\"password\":\"senha-segura-123\"}"))
                .andExpect(status().isConflict());
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"email\":\"invalido\",\"password\":\"curta\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginDevolveJwtEBearerAutenticaSemGuardarSessao() throws Exception {
        users.create("Ana", "ana@example.com", SENHA);
        var result = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("ANA@example.com", SENHA)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.usuario.email").value("ana@example.com"))
                .andExpect(jsonPath("$.usuario.passwordHash").doesNotExist()).andReturn();
        String token = tokenDaResposta(result.getResponse().getContentAsString());
        assertEquals(3, token.split("\\.").length);
        assertNull(result.getRequest().getSession(false));
        mvc.perform(get("/auth/me").header("Authorization", bearer(token))).andExpect(status().isOk());
        mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void credenciaisErradasAusentesOuDesativadasRetornamMesmoErro() throws Exception {
        User ana = users.create("Ana", "ana@example.com", SENHA);
        for (String email : new String[]{"ana@example.com", "ausente@example.com"}) {
            mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson(email, "senha-errada")))
                    .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.detail").value("E-mail ou senha inválidos."));
        }
        users.deactivate(ana.getId());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson(ana.getEmail(), SENHA)))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.detail").value("E-mail ou senha inválidos."));
    }

    @Test
    void loginRejeitaJsonInvalidoECamposInvalidos() throws Exception {
        for (String body : new String[]{"{", "{}", loginJson("nao-email", SENHA), loginJson("ana@example.com", "a".repeat(73))}) {
            mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
        }
    }

    @Test
    void rotasProtegidasExigemBearerELogoutDependeDoDescarteNoCliente() throws Exception {
        for (String rota : new String[]{"/users", "/planos-intercambio", "/cursos", "/auth/me"}) {
            mvc.perform(get(rota)).andExpect(status().isUnauthorized());
        }
        User ana = users.create("Ana", "ana@example.com", SENHA);
        String token = login(ana);
        mvc.perform(get("/auth/me").header("Authorization", "Basic YW5hOnNlbmhh" )).andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/logout").header("Authorization", bearer(token))).andExpect(status().isNoContent());
        mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
        // Conforme o JWT da aula, não há lista de revogação no servidor.
        mvc.perform(get("/auth/me").header("Authorization", bearer(token))).andExpect(status().isOk());
    }

    @Test
    void usuarioAcessaPropriosDadosENaoOutrosUsuarios() throws Exception {
        User ana = users.create("Ana", "ana@example.com", SENHA);
        User bia = users.create("Bia", "bia@example.com", SENHA);
        String token = login(ana);
        mvc.perform(get("/users/{id}", ana.getId()).header("Authorization", bearer(token))).andExpect(status().isOk());
        mvc.perform(get("/users/{id}/perfil", ana.getId()).header("Authorization", bearer(token))).andExpect(status().isNotFound());
        for (String rota : new String[]{"/users", "/users/" + bia.getId(), "/users/" + bia.getId() + "/perfil"}) {
            mvc.perform(get(rota).header("Authorization", bearer(token))).andExpect(status().isForbidden());
        }
        mvc.perform(put("/users/{id}", bia.getId()).header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Invadido\",\"email\":\"x@example.com\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/users/{id}", bia.getId()).header("Authorization", bearer(token))).andExpect(status().isForbidden());
        mvc.perform(patch("/users/{id}/activate", ana.getId()).header("Authorization", bearer(token))).andExpect(status().isForbidden());
        mvc.perform(put("/users/{id}", ana.getId()).header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana Maria\",\"email\":\"nova@example.com\"}"))
                .andExpect(status().isOk());
        // O subject é o e-mail, como na aula: mudar o e-mail exige um novo login.
        mvc.perform(get("/auth/me").header("Authorization", bearer(token))).andExpect(status().isUnauthorized());
        String novoToken = login(ana);
        mvc.perform(get("/auth/me").header("Authorization", bearer(novoToken))).andExpect(status().isOk());
        assertEquals("Bia", usuarios.findById(bia.getId()).orElseThrow().getNome());
    }

    @Test
    void listaSomentePlanosPropriosEImpedeForjarDonoNoCadastro() throws Exception {
        User ana = users.create("Ana", "ana@example.com", SENHA);
        User bia = users.create("Bia", "bia@example.com", SENHA);
        PlanoIntercambio proprio = plano(ana);
        plano(bia);
        String token = login(ana);
        mvc.perform(get("/planos-intercambio").header("Authorization", bearer(token))).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(proprio.getId()));
        String body = "{\"titulo\":\"Novo\",\"tipoIntercambio\":\"CURSO_IDIOMA\",\"status\":\"RASCUNHO\"}";
        mvc.perform(post("/planos-intercambio").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mvc.perform(post("/planos-intercambio").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content(body.replace("{", "{\"usuarioId\":" + bia.getId() + ",")))
                .andExpect(status().isForbidden());
        assertEquals(3, planoRepository.count());
    }

    @Test
    void bloqueiaPlanoTarefaEPreparacaoDeOutroDono() throws Exception {
        User ana = users.create("Ana", "ana@example.com", SENHA);
        User bia = users.create("Bia", "bia@example.com", SENHA);
        PlanoIntercambio proprio = plano(ana);
        PlanoIntercambio alheio = plano(bia);
        String token = login(ana);
        String base = "/planos-intercambio/" + alheio.getId();
        for (String rota : new String[]{base, base + "/tarefas", base + "/tarefas/1", base + "/preparacoes-idioma", base + "/preparacoes-idioma/1"}) {
            mvc.perform(get(rota).header("Authorization", bearer(token))).andExpect(status().isForbidden());
        }
        mvc.perform(post(base + "/tarefas/gerar-checklist").header("Authorization", bearer(token))).andExpect(status().isForbidden());
        mvc.perform(patch(base + "/tarefas/1/concluir").header("Authorization", bearer(token))).andExpect(status().isForbidden());
        mvc.perform(post(base + "/preparacoes-idioma").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/planos-intercambio/{id}/tarefas", proprio.getId()).header("Authorization", bearer(token))).andExpect(status().isOk());
        mvc.perform(get("/planos-intercambio/{id}/preparacoes-idioma", proprio.getId()).header("Authorization", bearer(token))).andExpect(status().isOk());
    }

    @Test
    void apenasAdminMantemCatalogosEConsultaListaDeUsuarios() throws Exception {
        User ana = users.create("Ana", "ana@example.com", SENHA);
        String token = login(ana);
        for (String rota : new String[]{"/instituicoes", "/cursos", "/area-estudo", "/modelos-tarefa"}) {
            mvc.perform(get(rota).header("Authorization", bearer(token))).andExpect(status().isOk());
            mvc.perform(post(rota).header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isForbidden());
        }
        ana.setRole(Role.ADMIN);
        usuarios.saveAndFlush(ana);
        mvc.perform(get("/users").header("Authorization", bearer(token))).andExpect(status().isOk());
        mvc.perform(post("/area-estudo").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Computação\",\"descricao\":\"Área teste\"}"))
                .andExpect(status().isCreated());
        ana.setRole(Role.USER);
        usuarios.saveAndFlush(ana);
        mvc.perform(get("/users").header("Authorization", bearer(token))).andExpect(status().isForbidden());
    }

    @Test
    void contaDesativadaNaoPodeUsarTokenAnterior() throws Exception {
        User ana = users.create("Ana", "ana@example.com", SENHA);
        String token = login(ana);
        users.deactivate(ana.getId());
        mvc.perform(get("/auth/me").header("Authorization", bearer(token))).andExpect(status().isUnauthorized());
    }

    @Test
    void trocaDeSenhaExigeNovaSenhaNosProximosLogins() throws Exception {
        User ana = users.create("Ana", "ana@example.com", SENHA);
        String token = login(ana);
        mvc.perform(patch("/users/{id}/password", ana.getId()).header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"senha-segura-123\",\"newPassword\":\"nova-senha-segura\"}"))
                .andExpect(status().isNoContent());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson(ana.getEmail(), SENHA)))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson(ana.getEmail(), "nova-senha-segura")))
                .andExpect(status().isOk());
    }

    @Test
    void recusaJwtMalformadoAdulteradoExpiradoEOuOutraChaveSemErro500() throws Exception {
        User ana = users.create("Ana", "ana@example.com", SENHA);
        String token = login(ana);
        String alterado = token.substring(0, token.lastIndexOf('.') + 1) + "assinatura-invalida";
        String expirado = Jwts.builder().subject(ana.getEmail()).expiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256).compact();
        String outraChave = Jwts.builder().subject(ana.getEmail()).expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(Keys.hmacShaKeyFor("outra-chave-de-testes-com-mais-de-32-caracteres".getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256).compact();
        for (String invalido : new String[]{"invalido", alterado, expirado, outraChave}) {
            mvc.perform(get("/auth/me").header("Authorization", bearer(invalido))).andExpect(status().isUnauthorized());
        }
    }

    private String login(User usuario) throws Exception {
        var result = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson(usuario.getEmail(), SENHA)))
                .andExpect(status().isOk()).andReturn();
        return tokenDaResposta(result.getResponse().getContentAsString());
    }
    private String tokenDaResposta(String json) { return JsonMapper.builder().build().readTree(json).get("token").asString(); }
    private String bearer(String token) { return "Bearer " + token; }
    private String loginJson(String email, String password) { return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"; }
    private PlanoIntercambio plano(User usuario) {
        return planos.adicionarPlano(usuario.getId(), "Plano teste", TipoIntercambio.CURSO_IDIOMA,
                StatusPlano.RASCUNHO, null, null, null, null, false);
    }
}
