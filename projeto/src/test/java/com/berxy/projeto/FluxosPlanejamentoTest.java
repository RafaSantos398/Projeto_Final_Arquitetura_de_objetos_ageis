package com.berxy.projeto;

import com.berxy.projeto.Curso.Curso;
import com.berxy.projeto.AreaEstudo.AreaEstudo;
import com.berxy.projeto.AreaEstudo.AreaEstudoRepository;
import com.berxy.projeto.Curso.CursoRepository;
import com.berxy.projeto.Curso.CursoService;
import com.berxy.projeto.Insituicao.Instituicao;
import com.berxy.projeto.Insituicao.InstituicaoRepository;
import com.berxy.projeto.ModeloTarefa.CategoriaTarefa;
import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import com.berxy.projeto.ModeloTarefa.ModeloTarefaRepository;
import com.berxy.projeto.PlanoIntercambio.*;
import com.berxy.projeto.PreparacaoIdioma.PreparacaoIdioma;
import com.berxy.projeto.PreparacaoIdioma.PreparacaoIdiomaRepository;
import com.berxy.projeto.TarefaPlano.TarefaPlano;
import com.berxy.projeto.TarefaPlano.TarefaPlanoRepository;
import com.berxy.projeto.perfilIntercambista.PerfilRepository;
import com.berxy.projeto.user.User;
import com.berxy.projeto.user.UserRepository;
import com.berxy.projeto.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FluxosPlanejamentoTest {
    @Autowired MockMvc mvc;
    @Autowired UserService users;
    @Autowired UserRepository usuarios;
    @Autowired PerfilRepository perfis;
    @Autowired InstituicaoRepository instituicoes;
    @Autowired CursoRepository cursos;
    @Autowired CursoService cursoService;
    @Autowired ModeloTarefaRepository modelos;
    @Autowired PlanoIntercambioRepository planos;
    @Autowired PlanoIntercambioService planoService;
    @Autowired TarefaPlanoRepository tarefas;
    @Autowired PreparacaoIdiomaRepository preparacoes;
    @Autowired AreaEstudoRepository areas;
    @Autowired DataSource dataSource;
    @Autowired org.springframework.web.context.WebApplicationContext context;
    private boolean bancoSeguro;

    @BeforeEach
    void exigirBancoIsolado() throws Exception {
        AutenticacaoTeste.exigirBancoIsolado(dataSource);
        bancoSeguro = true;
        mvc = AutenticacaoTeste.comoAdministrador(context);
    }

    // Sem transação no teste: cada chamada precisa funcionar com o limite real
    // do service e com spring.jpa.open-in-view=false.
    @AfterEach
    void limparDados() {
        if (!bancoSeguro) {
            return;
        }
        tarefas.deleteAll();
        preparacoes.deleteAll();
        planos.deleteAll();
        areas.deleteAll();
        cursos.deleteAll();
        modelos.deleteAll();
        instituicoes.deleteAll();
        perfis.deleteAll();
        usuarios.deleteAll();
    }

    @Test
    void consultaDoPlanoRetornaOCursoSelecionadoAposRecarregar() throws Exception {
        Cenario cenario = cenario(true);
        mvc.perform(get("/planos-intercambio/{id}", cenario.planoId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursoSelecionado").value(true))
                .andExpect(jsonPath("$.cursoId").value(cenario.cursoId()))
                .andExpect(jsonPath("$.cursoNome").isNotEmpty());
        Cenario semCurso = cenario(false);
        mvc.perform(get("/planos-intercambio/{id}", semCurso.planoId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursoSelecionado").value(false))
                .andExpect(jsonPath("$.cursoId").isEmpty())
                .andExpect(jsonPath("$.cursoNome").isEmpty());
    }

    @Test
    void checklistUsaModelosAtivosDoCursoEPreservaProgresso() throws Exception {
        Cenario cenario = cenario(true);
        ModeloTarefa passaporte = modelo("Passaporte");
        ModeloTarefa idioma = modelo("Comprovante de idioma");
        ModeloTarefa inativo = modelo("Modelo inativo");
        cursoService.associarModelo(cenario.cursoId(), passaporte.getId());
        cursoService.associarModelo(cenario.cursoId(), idioma.getId());
        cursoService.associarModelo(cenario.cursoId(), inativo.getId());
        inativo.desativar();
        modelos.save(inativo);
        modelo("Modelo de outro curso");

        mvc.perform(post(rota(cenario) + "/gerar-checklist"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        TarefaPlano tarefa = tarefas.findByPlanoIdAndModeloOrigemId(
                cenario.planoId(), passaporte.getId()).orElseThrow();
        String prazo = LocalDate.now().plusDays(30).toString();
        mvc.perform(patch(rota(cenario) + "/{id}/prazo", tarefa.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prazo\":\"" + prazo + "\"}"))
                .andExpect(status().isOk());
        mvc.perform(patch(rota(cenario) + "/{id}/concluir", tarefa.getId()))
                .andExpect(status().isOk());
        Instant concluidaEm = tarefas.findById(tarefa.getId()).orElseThrow().getConcluidaEm();

        passaporte.atualizarConteudo("Novo título", "Nova descrição", CategoriaTarefa.DOCUMENTACAO);
        passaporte.desativar();
        modelos.save(passaporte);
        ModeloTarefa novo = modelo("Nova tarefa aplicável");
        cursoService.associarModelo(cenario.cursoId(), novo.getId());

        mvc.perform(post(rota(cenario) + "/gerar-checklist"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
        mvc.perform(post(rota(cenario) + "/gerar-checklist"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
        mvc.perform(get(rota(cenario) + "/{id}", tarefa.getId()))
                .andExpect(jsonPath("$.titulo").value("Passaporte"))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.prazo").value(prazo))
                .andExpect(jsonPath("$.plano").doesNotExist());
        assertEquals(concluidaEm, tarefas.findById(tarefa.getId()).orElseThrow().getConcluidaEm());
        assertEquals(3, tarefas.findByPlanoIdOrderByIdAsc(cenario.planoId()).size());
        assertTrue(tarefas.findByPlanoIdAndModeloOrigemId(cenario.planoId(), inativo.getId()).isEmpty());
    }

    @Test
    void geracaoIndividualAceitaSomenteModeloAtivoAplicavel() throws Exception {
        Cenario cenario = cenario(true);
        ModeloTarefa aplicavel = modelo("Aplicável");
        ModeloTarefa outro = modelo("Não associado");
        ModeloTarefa inativo = modelo("Inativo");
        cursoService.associarModelo(cenario.cursoId(), aplicavel.getId());
        cursoService.associarModelo(cenario.cursoId(), inativo.getId());
        inativo.desativar();
        modelos.save(inativo);

        gerarIndividual(cenario, outro.getId(), 400);
        gerarIndividual(cenario, inativo.getId(), 400);
        gerarIndividual(cenario, Long.MAX_VALUE, 404);
        mvc.perform(post(rota(cenario) + "/gerar").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        gerarIndividual(cenario, aplicavel.getId(), 200);
        gerarIndividual(cenario, aplicavel.getId(), 200);
        assertEquals(1, tarefas.findByPlanoIdOrderByIdAsc(cenario.planoId()).size());
    }

    @Test
    void checklistExigePlanoComCursoEPermiteCursoSemModelos() throws Exception {
        Cenario semCurso = cenario(false);
        mvc.perform(post(rota(semCurso) + "/gerar-checklist"))
                .andExpect(status().isConflict());
        Cenario vazio = cenario(true);
        mvc.perform(post(rota(vazio) + "/gerar-checklist"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(post("/planos-intercambio/9223372036854775807/tarefas/gerar-checklist"))
                .andExpect(status().isNotFound());
        assertEquals(0, tarefas.count());
    }

    @Test
    void transicoesPreservamConclusaoEImpedeTrocaDeCursoOuPlanoErrado() throws Exception {
        Cenario cenario = cenario(true);
        ModeloTarefa modelo = modelo("Documento");
        cursoService.associarModelo(cenario.cursoId(), modelo.getId());
        gerarIndividual(cenario, modelo.getId(), 200);
        TarefaPlano tarefa = tarefas.findByPlanoIdAndModeloOrigemId(cenario.planoId(), modelo.getId()).orElseThrow();
        String base = rota(cenario) + "/" + tarefa.getId();
        mvc.perform(patch(base + "/iniciar")).andExpect(status().isOk());
        mvc.perform(patch(base + "/iniciar")).andExpect(status().isConflict());
        mvc.perform(patch(base + "/concluir")).andExpect(status().isOk());
        Instant concluidaEm = tarefas.findById(tarefa.getId()).orElseThrow().getConcluidaEm();
        mvc.perform(patch(base + "/concluir")).andExpect(status().isOk());
        assertEquals(concluidaEm, tarefas.findById(tarefa.getId()).orElseThrow().getConcluidaEm());
        mvc.perform(patch(base + "/reabrir"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PENDENTE"));
        assertNull(tarefas.findById(tarefa.getId()).orElseThrow().getConcluidaEm());
        mvc.perform(patch(base + "/prazo").contentType(MediaType.APPLICATION_JSON).content("{\"prazo\":null}"))
                .andExpect(status().isOk());

        Cenario outro = cenario(true);
        mvc.perform(get(rota(outro) + "/{id}", tarefa.getId())).andExpect(status().isNotFound());
        mvc.perform(patch(rota(outro) + "/{id}/concluir", tarefa.getId())).andExpect(status().isNotFound());
        mvc.perform(patch("/planos-intercambio/{id}/curso", cenario.planoId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cursoId\":" + outro.cursoId() + "}"))
                .andExpect(status().isConflict());
        assertEquals(cenario.cursoId(), planos.findById(cenario.planoId()).orElseThrow().getCurso().getId());
    }

    @Test
    void preparacaoPersisteAtualizaProgressoEPermiteNovaAposConclusao() throws Exception {
        Cenario cenario = cenario(true);
        String base = preparacoes(cenario);
        mvc.perform(post(base).contentType(MediaType.APPLICATION_JSON).content(preparacao(" PT-br ", 120)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.idioma").value("pt-BR"))
                .andExpect(jsonPath("$.status").value("NAO_INICIADA"))
                .andExpect(jsonPath("$.planoId").value(cenario.planoId()));
        PreparacaoIdioma preparacao = preparacoes.findByPlanoIntercambioId(cenario.planoId()).getFirst();
        String item = base + "/" + preparacao.getId();
        mvc.perform(patch(item + "/iniciar")).andExpect(status().isOk());
        mvc.perform(patch(item + "/iniciar")).andExpect(status().isConflict());
        mvc.perform(put(item).contentType(MediaType.APPLICATION_JSON).content(preparacao("pt-BR", 240)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
        mvc.perform(get(item)).andExpect(jsonPath("$.minutosSemanaisPlanejados").value(240));
        mvc.perform(patch(item + "/concluir")).andExpect(status().isOk());
        mvc.perform(patch(item + "/concluir")).andExpect(status().isOk());
        mvc.perform(post(base).contentType(MediaType.APPLICATION_JSON).content(preparacao("pt-br", 60)))
                .andExpect(status().isCreated());
        mvc.perform(get(base)).andExpect(jsonPath("$.length()").value(2));

        Cenario outro = cenario(true);
        mvc.perform(get(preparacoes(outro) + "/{id}", preparacao.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void preparacaoDuplicadaNaoMudaDadosJaPersistidos() throws Exception {
        Cenario cenario = cenario(true);
        String base = preparacoes(cenario);
        mvc.perform(post(base).contentType(MediaType.APPLICATION_JSON).content(preparacao("en", 120)))
                .andExpect(status().isCreated());
        mvc.perform(post(base).contentType(MediaType.APPLICATION_JSON).content(preparacao("EN", 120)))
                .andExpect(status().isConflict());
        mvc.perform(post(base).contentType(MediaType.APPLICATION_JSON).content(preparacao("fr", 180)))
                .andExpect(status().isCreated());
        PreparacaoIdioma frances = preparacoes.findByPlanoIntercambioId(cenario.planoId()).stream()
                .filter(p -> p.getIdioma().equals("fr")).findFirst().orElseThrow();
        mvc.perform(put(base + "/{id}", frances.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(preparacao(" EN ", 300)))
                .andExpect(status().isConflict());
        mvc.perform(get(base + "/{id}", frances.getId()))
                .andExpect(jsonPath("$.idioma").value("fr"))
                .andExpect(jsonPath("$.minutosSemanaisPlanejados").value(180));
    }

    @Test
    void preparacaoRejeitaDadosInvalidosSemCriarRegistros() throws Exception {
        Cenario cenario = cenario(true);
        String base = preparacoes(cenario);
        String[] invalidos = {
                preparacao("pt-BR-extra", 120), preparacao("en", 0), preparacao("en", -1),
                preparacao("en", 120).replace("\"B2\"", "null"),
                preparacao("en", 120).replace("\"" + LocalDate.now().plusDays(45) + "\"", "null")
        };
        for (String corpo : invalidos) {
            mvc.perform(post(base).contentType(MediaType.APPLICATION_JSON).content(corpo))
                    .andExpect(status().isBadRequest());
        }
        assertTrue(preparacoes.findByPlanoIntercambioId(cenario.planoId()).isEmpty());
    }

    @Test
    void perfilDaBranchDeJoaoFuncionaSemTransacaoNoTeste() throws Exception {
        Cenario cenario = cenario(false);
        String rota = "/users/" + cenario.usuarioId() + "/perfil";
        String corpo = """
                {"nascimento":"2000-01-01","nacionalidades":["Brasileira"],
                 "paisResidencia":"Brasil","proficienciaIdioma":[{"idioma":"EN","nivel":"B1"}],
                 "situacaoEscolaridade":"CURSANDO","nivelEscolaridade":"GRADUACAO"}
                """;
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(cenario.usuarioId()));
        mvc.perform(get(rota)).andExpect(status().isOk())
                .andExpect(jsonPath("$.proficienciaIdioma[0].idioma").value("en"));
        mvc.perform(put(rota).contentType(MediaType.APPLICATION_JSON).content(corpo.replace("B1", "B2")))
                .andExpect(status().isOk());
        mvc.perform(get(rota)).andExpect(jsonPath("$.proficienciaIdioma[0].nivel").value("B2"));
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isConflict());
    }

    @Test
    void modelosValidamEntradasECobremCadastroConsultaAtualizacaoEDesativacao() throws Exception {
        String corpo = "{\"titulo\":\" Passaporte \",\"descricao\":\"Documento\",\"categoria\":\"DOCUMENTACAO\"}";
        mvc.perform(post("/modelos-tarefa").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.titulo").value("Passaporte"));
        Long id = modelos.findAll().getFirst().getId();
        mvc.perform(get("/modelos-tarefa/{id}", id)).andExpect(status().isOk());
        mvc.perform(put("/modelos-tarefa/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(corpo.replace("Passaporte", "Visto"))).andExpect(status().isOk());
        mvc.perform(patch("/modelos-tarefa/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
        mvc.perform(get("/modelos-tarefa")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(post("/modelos-tarefa").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo.replace("\"DOCUMENTACAO\"", "null"))).andExpect(status().isBadRequest());
        mvc.perform(post("/modelos-tarefa").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo.replace(" Passaporte ", " "))).andExpect(status().isBadRequest());
        mvc.perform(get("/modelos-tarefa/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void areaValidaEntradasENaoEncontradoERespondeCrud() throws Exception {
        mvc.perform(post("/area-estudo").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\" Computação \",\"descricao\":\" \"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.nome").value("Computação"));
        AreaEstudo area = areas.findAll().getFirst();
        mvc.perform(get("/area-estudo/{id}", area.getId())).andExpect(status().isOk());
        mvc.perform(put("/area-estudo/{id}", area.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Idiomas\",\"descricao\":null}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Idiomas"));
        mvc.perform(get("/area-estudo")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/area-estudo/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
        mvc.perform(post("/area-estudo").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\" \",\"descricao\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void instituicaoRespondeCrudERejeitaSiteInvalido() throws Exception {
        String corpo = "{\"nome\":\" Instituto \",\"descricao\":null,\"siteOficial\":\"https://example.org\"}";
        mvc.perform(post("/instituicoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated());
        Long id = instituicoes.findAll().getFirst().getId();
        mvc.perform(get("/instituicoes/{id}", id)).andExpect(status().isOk());
        mvc.perform(put("/instituicoes/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(corpo.replace(" Instituto ", "Universidade")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Universidade"));
        mvc.perform(get("/instituicoes")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/instituicoes/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
        mvc.perform(post("/instituicoes").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo.replace("https://example.org", "javascript:alert(1)")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cursoRespondeCrudAtivacaoEFiltroEAssociacoesDeModelos() throws Exception {
        Cenario cenario = cenario(true);
        Long instituicaoId = cursos.findById(cenario.cursoId()).orElseThrow().getInstituicao().getId();
        String corpo = cursoJson(instituicaoId, "pt-br");
        mvc.perform(post("/cursos").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.idioma").value("pt-BR"));
        mvc.perform(get("/cursos")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/cursos").param("instituicaoId", instituicaoId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/cursos/{id}", cenario.cursoId())).andExpect(status().isOk());
        mvc.perform(put("/cursos/{id}", cenario.cursoId()).contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk());
        mvc.perform(patch("/cursos/{id}/desativar", cenario.cursoId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
        mvc.perform(patch("/cursos/{id}/ativar", cenario.cursoId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
        ModeloTarefa modelo = modelo("Documento do curso");
        String vinculo = "/cursos/" + cenario.cursoId() + "/modelos-tarefa/" + modelo.getId();
        mvc.perform(post(vinculo)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(post(vinculo)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/cursos/{id}/modelos-tarefa", cenario.cursoId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(delete(vinculo)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void cursoRejeitaIdiomaComSegmentosExtrasEInstituicaoInexistente() throws Exception {
        Cenario cenario = cenario(true);
        Long instituicaoId = cursos.findById(cenario.cursoId()).orElseThrow().getInstituicao().getId();
        mvc.perform(post("/cursos").contentType(MediaType.APPLICATION_JSON)
                        .content(cursoJson(instituicaoId, "pt-BR-extra")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/cursos").contentType(MediaType.APPLICATION_JSON)
                        .content(cursoJson(Long.MAX_VALUE, "en")))
                .andExpect(status().isNotFound());
    }

    @Test
    void planoRespondeCrudEValidaDadosSemPerderPlanejamento() throws Exception {
        Cenario cenario = cenario(false);
        String corpo = """
                {"usuarioId":%d,"titulo":"Plano via API","tipoIntercambio":"CURSO_IDIOMA",
                 "status":"RASCUNHO","orcamentoTotal":1000.50,"moeda":"brl","duracaoMeses":6}
                """.formatted(cenario.usuarioId());
        mvc.perform(post("/planos-intercambio").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.moeda").value("BRL"));
        String item = "/planos-intercambio/" + cenario.planoId();
        mvc.perform(put(item).contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk()).andExpect(jsonPath("$.titulo").value("Plano via API"));
        mvc.perform(patch(item + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"EM_ANDAMENTO\"}"))
                .andExpect(status().isOk());
        mvc.perform(put(item).contentType(MediaType.APPLICATION_JSON).content(corpo.replace("1000.50", "-1")))
                .andExpect(status().isBadRequest());
        mvc.perform(get(item)).andExpect(status().isOk()).andExpect(jsonPath("$.orcamentoTotal").value(1000.50))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
        mvc.perform(get("/planos-intercambio")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/planos-intercambio/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void usuarioDesativadoNaoPodeReceberNovoPlano() throws Exception {
        Cenario cenario = cenario(false);
        users.deactivate(cenario.usuarioId());
        String corpo = """
                {"usuarioId":%d,"titulo":"Plano","tipoIntercambio":"CURSO_IDIOMA","status":"RASCUNHO"}
                """.formatted(cenario.usuarioId());
        mvc.perform(post("/planos-intercambio").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isNotFound());
        assertEquals(1, planos.count());
    }

    private String cursoJson(Long instituicaoId, String idioma) {
        return """
                {"instituicaoId":%d,"nome":"Curso via API","descricao":null,
                 "tipoIntercambio":"CURSO_IDIOMA","idioma":"%s","duracaoMeses":6}
                """.formatted(instituicaoId, idioma);
    }

    private Cenario cenario(boolean selecionarCurso) {
        User usuario = users.create("Rafael", UUID.randomUUID() + "@example.com", "senha-forte");
        Instituicao instituicao = instituicoes.save(new Instituicao("Instituição", null, null));
        Curso curso = cursos.save(new Curso(
                instituicao, "Curso de inglês", null, TipoIntercambio.CURSO_IDIOMA, "en", 6
        ));
        PlanoIntercambio plano = planoService.adicionarPlano(
                usuario.getId(), "Meu intercâmbio", TipoIntercambio.CURSO_IDIOMA,
                StatusPlano.RASCUNHO, null, null, null, null, null
        );
        if (selecionarCurso) {
            planoService.selecionarCurso(plano.getId(), curso.getId());
        }
        return new Cenario(plano.getId(), curso.getId(), usuario.getId());
    }

    private ModeloTarefa modelo(String titulo) {
        return modelos.save(new ModeloTarefa(titulo, "Descrição do modelo", CategoriaTarefa.DOCUMENTACAO));
    }

    private String rota(Cenario cenario) {
        return "/planos-intercambio/" + cenario.planoId() + "/tarefas";
    }

    private String preparacoes(Cenario cenario) {
        return "/planos-intercambio/" + cenario.planoId() + "/preparacoes-idioma";
    }

    private String preparacao(String idioma, int minutos) {
        return """
                {"idioma":"%s","nivelAlvo":"B2","dataMeta":"%s","minutosSemanaisPlanejados":%d}
                """.formatted(idioma, LocalDate.now().plusDays(45), minutos);
    }

    private void gerarIndividual(Cenario cenario, Long modeloId, int resultado) throws Exception {
        mvc.perform(post(rota(cenario) + "/gerar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"modeloOrigemId\":" + modeloId + "}"))
                .andExpect(status().is(resultado));
    }

    private record Cenario(Long planoId, Long cursoId, Long usuarioId) {
    }
}
