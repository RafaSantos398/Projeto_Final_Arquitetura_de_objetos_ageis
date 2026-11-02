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
        if (instituicaoId == null) {
            return repository.findAll();
        }
        buscarInstituicao(instituicaoId);
        return repository.findByInstituicaoId(instituicaoId);
    }

    @Transactional(readOnly = true)
    public Curso buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado."));
    }

    public Curso adicionarCurso(CursoRequest dados) {
        Instituicao instituicao = buscarInstituicao(dados.instituicaoId());
        Curso curso = new Curso(
                instituicao, dados.nome(), dados.descricao(),
                dados.tipoIntercambio(), dados.idioma(), dados.duracaoMeses()
        );
        return repository.save(curso);
    }

    public Curso atualizarCurso(Long id, CursoRequest dados) {
        Curso curso = buscar(id);
        Instituicao instituicao = buscarInstituicao(dados.instituicaoId());
        curso.atualizarConteudo(
                instituicao, dados.nome(), dados.descricao(),
                dados.tipoIntercambio(), dados.idioma(), dados.duracaoMeses()
        );
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
        curso.adicionarModeloTarefa(buscarModelo(modeloId));
        return repository.save(curso).getModelosTarefaAtivos();
    }

    public List<ModeloTarefa> desassociarModelo(Long cursoId, Long modeloId) {
        Curso curso = buscar(cursoId);
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