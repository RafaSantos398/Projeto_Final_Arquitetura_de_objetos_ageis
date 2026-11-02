package com.berxy.projeto.TarefaPlano;

import com.berxy.projeto.Curso.CursoService;
import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import com.berxy.projeto.ModeloTarefa.ModeloTarefaRepository;
import com.berxy.projeto.PlanoIntercambio.PlanoIntercambio;
import com.berxy.projeto.PlanoIntercambio.PlanoIntercambioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TarefaPlanoService {
    @Autowired
    private TarefaPlanoRepository tarefaPlanoRepository;

    @Autowired
    private PlanoIntercambioService planoIntercambioService;

    @Autowired
    private ModeloTarefaRepository modeloTarefaRepository;

    @Autowired
    private CursoService cursoService;

    public List<TarefaPlano> list(Long planoId) {
        planoIntercambioService.GetByid(planoId);
        return tarefaPlanoRepository.findByPlanoIdOrderByIdAsc(planoId);
    }

    public List<TemaChecklist.Resposta> temas(Long planoId) {
        planoIntercambioService.GetByid(planoId);
        return java.util.Arrays.stream(TemaChecklist.values()).map(TemaChecklist.Resposta::de).toList();
    }

    @Transactional
    public List<TarefaPlano> gerarTemas(Long planoId, List<TemaChecklist> temas) {
        if (temas == null || temas.isEmpty() || temas.contains(null)) {
            throw new IllegalArgumentException("Selecione pelo menos um tema válido.");
        }
        PlanoIntercambio plano = planoIntercambioService.bloquearPlano(planoId);
        for (TemaChecklist tema : temas.stream().distinct().toList()) {
            for (TemaChecklist.Sugestao sugestao : tema.getSugestoes()) {
                if (!tarefaPlanoRepository.existsByPlanoIdAndChaveSugestao(planoId, tema.name() + ":" + sugestao.chave())) {
                    tarefaPlanoRepository.save(TarefaPlano.sugerida(plano, tema, sugestao));
                }
            }
        }
        return tarefaPlanoRepository.findByPlanoIdOrderByIdAsc(planoId);
    }

    @Transactional
    public TarefaPlano criar(Long planoId, TarefaPlanoRequest.ConteudoRequest dados) {
        PlanoIntercambio plano = planoIntercambioService.GetByid(planoId);
        return tarefaPlanoRepository.save(new TarefaPlano(plano, dados.titulo(), dados.descricao(), dados.categoria(), dados.prazo()));
    }

    @Transactional
    public TarefaPlano atualizar(Long planoId, Long id, TarefaPlanoRequest.ConteudoRequest dados) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.atualizarConteudo(dados.titulo(), dados.descricao(), dados.categoria(), dados.prazo());
        return tarefaPlanoRepository.save(tarefa);
    }

    @Transactional
    public void remover(Long planoId, Long id) {
        tarefaPlanoRepository.delete(GetByid(planoId, id));
    }

    public TarefaPlano GetByid(Long planoId, Long id) {
        planoIntercambioService.GetByid(planoId);
        return tarefaPlanoRepository.findByIdAndPlanoId(id, planoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tarefa não encontrada neste plano."
                ));
    }

    @Transactional
    public TarefaPlano gerarTarefa(Long planoId, Long modeloOrigemId) {
        PlanoIntercambio plano = planoIntercambioService.GetByid(planoId);
        if (modeloOrigemId == null) {
            throw new IllegalArgumentException("O modelo de tarefa é obrigatório.");
        }
        Optional<TarefaPlano> existente = tarefaPlanoRepository
                .findByPlanoIdAndModeloOrigemId(planoId, modeloOrigemId);
        if (existente.isPresent()) {
            return existente.get();
        }

        ModeloTarefa modelo = modeloTarefaRepository.findById(modeloOrigemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Modelo de tarefa não encontrado."
                ));
        boolean aplicavel = false;
        for (ModeloTarefa modeloDoCurso : modelosAtivosDoCurso(plano)) {
            if (modeloOrigemId.equals(modeloDoCurso.getId())) {
                aplicavel = true;
                break;
            }
        }
        if (!aplicavel) {
            throw new IllegalArgumentException(
                    "O modelo deve estar ativo e associado ao curso escolhido."
            );
        }

        TarefaPlano tarefa = plano.gerarTarefa(modelo);
        return tarefaPlanoRepository.save(tarefa);
    }

    @Transactional
    public List<TarefaPlano> gerarChecklist(Long planoId) {
        PlanoIntercambio plano = planoIntercambioService.GetByid(planoId);
        for (ModeloTarefa modelo : modelosAtivosDoCurso(plano)) {
            if (tarefaPlanoRepository.findByPlanoIdAndModeloOrigemId(planoId, modelo.getId()).isEmpty()) {
                tarefaPlanoRepository.save(plano.gerarTarefa(modelo));
            }
        }
        // Inclui tarefas antigas para preservar o checklist e o progresso já existentes.
        return tarefaPlanoRepository.findByPlanoIdOrderByIdAsc(planoId);
    }

    private List<ModeloTarefa> modelosAtivosDoCurso(PlanoIntercambio plano) {
        if (plano.getCurso() == null) {
            throw new IllegalStateException("Selecione um curso antes de gerar tarefas.");
        }
        return cursoService.listarModelosAtivos(plano.getCurso().getId());
    }

    @Transactional
    public TarefaPlano iniciar(Long planoId, Long id) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.iniciar();
        return tarefaPlanoRepository.save(tarefa);
    }

    @Transactional
    public TarefaPlano mover(Long planoId, Long id, StatusTarefa status) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.moverPara(status);
        return tarefaPlanoRepository.save(tarefa);
    }

    @Transactional
    public TarefaPlano concluir(Long planoId, Long id) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.concluir();
        return tarefaPlanoRepository.save(tarefa);
    }

    @Transactional
    public TarefaPlano reabrir(Long planoId, Long id) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.reabrir();
        return tarefaPlanoRepository.save(tarefa);
    }

    @Transactional
    public TarefaPlano alterarPrazo(Long planoId, Long id, LocalDate prazo) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.alterarPrazo(prazo);
        return tarefaPlanoRepository.save(tarefa);
    }
}
