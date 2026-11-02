package com.berxy.projeto.perfilIntercambista;

import com.berxy.projeto.user.UserService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PerfilControllerTest {
    @Autowired org.springframework.web.context.WebApplicationContext context;
    @org.junit.jupiter.api.BeforeEach
    void autenticarAdministrador() throws Exception {
        mvc = com.berxy.projeto.AutenticacaoTeste.comoAdministrador(context);
    }
    @Autowired MockMvc mvc;
    @Autowired UserService users;
    @Autowired PerfilRepository repository;
    @Autowired EntityManager em;
    private static final String BODY = """
        {"nascimento":"2000-01-01","nacionalidades":[" Brasileira "],
         "paisResidencia":" Brasil ","proficienciaIdioma":[{"idioma":"EN","nivel":"B1"}],
         "situacaoEscolaridade":"CURSANDO","nivelEscolaridade":"GRADUACAO"}
        """;
    private String rota() {
        return "/users/" + users.create("Joao", "joao@example.com", "senha-forte").getId() + "/perfil";
    }
    @Test void cadastraPersisteEAtualizaComIdCompartilhado() throws Exception {
        String rota = rota();
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated()).andExpect(header().string("Location", org.hamcrest.Matchers.endsWith(rota)))
                .andExpect(jsonPath("$.proficienciaIdioma[0].idioma").value("en"))
                .andExpect(jsonPath("$.paisResidencia").value("Brasil"))
                .andExpect(jsonPath("$.user").doesNotExist());
        em.flush(); em.clear();
        Long id = Long.valueOf(rota.split("/")[2]);
        assertEquals(id, repository.findById(id).orElseThrow().getUser().getId());
        mvc.perform(get(rota)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        mvc.perform(put(rota).contentType(MediaType.APPLICATION_JSON).content(BODY.replace("B1", "B2")))
                .andExpect(status().isOk());
        em.flush(); em.clear();
        mvc.perform(get(rota)).andExpect(jsonPath("$.proficienciaIdioma[0].nivel").value("B2"));
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isConflict());
    }
    @Test void rejeitaAtualizacaoInvalidaSemPerderDados() throws Exception {
        String rota = rota();
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isCreated());
        String duplicado = BODY.replace("{\"idioma\":\"EN\",\"nivel\":\"B1\"}",
                "{\"idioma\":\"EN\",\"nivel\":\"B1\"},{\"idioma\":\"en\",\"nivel\":\"A1\"}");
        mvc.perform(put(rota).contentType(MediaType.APPLICATION_JSON).content(duplicado)).andExpect(status().isBadRequest());
        mvc.perform(get(rota)).andExpect(jsonPath("$.proficienciaIdioma.length()").value(1));
        mvc.perform(put(rota).contentType(MediaType.APPLICATION_JSON).content(BODY.replace("2000-01-01", "2999-01-01")))
                .andExpect(status().isBadRequest());
    }
    @Test void retorna404ParaPerfilOuUsuarioAusente() throws Exception {
        mvc.perform(get(rota())).andExpect(status().isNotFound());
        mvc.perform(post("/users/9223372036854775807/perfil").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNotFound());
    }
}
