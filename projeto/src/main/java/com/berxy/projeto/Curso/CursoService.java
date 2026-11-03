package com.berxy.projeto.Curso;

import com.berxy.projeto.Insituicao.Instituicao;
import com.berxy.projeto.Insituicao.InstituicaoRepository;
import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import com.berxy.projeto.ModeloTarefa.ModeloTarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class CursoService {
    @org.springframework.beans.factory.annotation.Autowired
    private com.berxy.projeto.security.AcessoService acesso;

    private com.berxy.projeto.user.User atual() {
        return acesso.usuarioAtual(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication());
    }

    private void conferirLeitura(Curso curso) {
        if (curso.isPessoal() && !curso.getProprietario().getId().equals(atual().getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Este curso pertence a outro usuário.");
        }
    }

    public Curso criarPessoal(CursoPessoalRequest dados) {
        Curso curso = Curso.pessoal(atual(), dados);
        curso.definirAreaEstudo(resolverArea(dados.areaEstudoId(), dados.areaEstudoNome()));
        return repository.save(curso);
    }

    public Curso atualizarPessoal(Long id, CursoPessoalRequest dados) {
        Curso curso = buscar(id);
        if (!curso.isPessoal()) throw new IllegalArgumentException("Use a administração para editar o catálogo.");
        curso.atualizarPessoal(dados);
        curso.definirAreaEstudo(resolverArea(dados.areaEstudoId(), dados.areaEstudoNome()));
        return repository.save(curso);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.berxy.projeto.AreaEstudo.AreaEstudoRepository areas;

    private com.berxy.projeto.AreaEstudo.AreaEstudo resolverArea(Long id, String nome) {
        String texto = nome == null ? null : nome.strip();
        if (texto != null && texto.isEmpty()) texto = null;
        if (id != null && texto != null) throw new IllegalArgumentException("Escolha uma área existente ou informe uma nova, não ambas.");
        if (id != null) return areas.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Área de estudo não encontrada."));
        if (texto == null) return null;
        com.berxy.projeto.AreaEstudo.AreaEstudo nova = new com.berxy.projeto.AreaEstudo.AreaEstudo(texto, null);
        var existente = areas.findFirstByNomeIgnoreCaseOrderByIdAsc(nova.getNome());
        return existente.isPresent() ? existente.get() : areas.save(nova);
    }

    private final CursoRepository repository;
    private final InstituicaoRepository instituicaoRepository;
    private final ModeloTarefaRepository modeloTarefaRepository;

    public CursoService(
            CursoRepository repository,
            InstituicaoRepository instituicaoRepository,
            ModeloTarefaRepository modeloTarefaRepository
    ) {
        this.repository = repository;
        this.instituicaoRepository = instituicaoRepository;
        this.modeloTarefaRepository = modeloTarefaRepository;
    }

    @Transactional(readOnly = true)
    public List<Curso> list(Long instituicaoId) {
        if (instituicaoId != null) buscarInstituicao(instituicaoId);
        com.berxy.projeto.user.User dono = atual();
        List<Curso> visiveis = new java.util.ArrayList<>();
        List<Curso> encontrados = instituicaoId == null ? repository.findAll() : repository.findByInstituicaoId(instituicaoId);
        for (Curso curso : encontrados) {
            if (!curso.isPessoal() || curso.getProprietario().getId().equals(dono.getId())) visiveis.add(curso);
        }
        return visiveis;
    }

    @Transactional(readOnly = true)
    public Curso buscar(Long id) {
        Curso curso = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado."));
        conferirLeitura(curso);
        return curso;
    }

    public Curso adicionarCurso(CursoRequest dados) {
        Instituicao instituicao = buscarInstituicao(dados.instituicaoId());
        Curso curso = new Curso(
                instituicao, dados.nome(), dados.descricao(),
                dados.tipoIntercambio(), dados.idioma(), dados.duracaoMeses()
        );
        curso.definirAreaEstudo(resolverArea(dados.areaEstudoId(), dados.areaEstudoNome()));
        return repository.save(curso);
    }

    public Curso atualizarCurso(Long id, CursoRequest dados) {
        Curso curso = buscar(id);
        if (curso.isPessoal()) throw new IllegalArgumentException("Use a edição de curso pessoal.");
        Instituicao instituicao = buscarInstituicao(dados.instituicaoId());
        curso.atualizarConteudo(
                instituicao, dados.nome(), dados.descricao(),
                dados.tipoIntercambio(), dados.idioma(), dados.duracaoMeses()
        );
        curso.definirAreaEstudo(resolverArea(dados.areaEstudoId(), dados.areaEstudoNome()));
        return repository.save(curso);
    }

    public Curso ativarCurso(Long id) {
        Curso curso = buscar(id);
        curso.ativar();
        return repository.save(curso);
    }

    public Curso desativarCurso(Long id) {
        Curso curso = buscar(id);
        curso.desativar();
        return repository.save(curso);
    }

    // Pode ser usado pelo TarefaPlanoService para gerar o checklist do curso.
    @Transactional(readOnly = true)
    public List<ModeloTarefa> listarModelosAtivos(Long cursoId) {
        return buscar(cursoId).getModelosTarefaAtivos();
    }

    public List<ModeloTarefa> associarModelo(Long cursoId, Long modeloId) {
        Curso curso = buscar(cursoId);
        if (curso.isPessoal()) throw new IllegalArgumentException("Cursos pessoais são organizados pelo cronograma.");
        curso.adicionarModeloTarefa(buscarModelo(modeloId));
        return repository.save(curso).getModelosTarefaAtivos();
    }

    public List<ModeloTarefa> desassociarModelo(Long cursoId, Long modeloId) {
        Curso curso = buscar(cursoId);
        if (curso.isPessoal()) throw new IllegalArgumentException("Cursos pessoais são organizados pelo cronograma.");
        curso.removerModeloTarefa(buscarModelo(modeloId));
        return repository.save(curso).getModelosTarefaAtivos();
    }

    private Instituicao buscarInstituicao(Long instituicaoId) {
        if (instituicaoId == null) {
            throw new IllegalArgumentException("A instituição é obrigatória.");
        }
        return instituicaoRepository.findById(instituicaoId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituição não encontrada."));
    }

    private ModeloTarefa buscarModelo(Long modeloId) {
        return modeloTarefaRepository.findById(modeloId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Modelo de tarefa não encontrado."));
    }
}