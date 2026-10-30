package com.berxy.projeto.TarefaPlano;

import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import com.berxy.projeto.ModeloTarefa.ModeloTarefaRepository;
import com.berxy.projeto.PlanoIntercambio.PlanoIntercambio;
import com.berxy.projeto.PlanoIntercambio.PlanoIntercambioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TarefaPlanoService {
    @Autowired
    private TarefaPlanoRepository tarefaPlanoRepository;

    @Autowired
    private PlanoIntercambioService planoIntercambioService;

    @Autowired
    private ModeloTarefaRepository modeloTarefaRepository;

    public List<TarefaPlano> list(Long planoId) {
        planoIntercambioService.GetByid(planoId);
        List<TarefaPlano> tarefasDoPlano = new ArrayList<>();
        for (TarefaPlano tarefa : tarefaPlanoRepository.findAll()) {
            if (planoId.equals(tarefa.getPlano().getId())) {
                tarefasDoPlano.add(tarefa);
            }
        }
        return tarefasDoPlano;
    }

    public TarefaPlano GetByid(Long planoId, Long id) {
        planoIntercambioService.GetByid(planoId);
        TarefaPlano tarefa = tarefaPlanoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa nao encontrada"));

        if (!planoId.equals(tarefa.getPlano().getId())) {
            throw new RuntimeException("Tarefa nao encontrada neste plano");
        }
        return tarefa;
    }

    public TarefaPlano gerarTarefa(Long planoId, Long modeloOrigemId) {
        PlanoIntercambio plano = planoIntercambioService.GetByid(planoId);

        for (TarefaPlano tarefa : list(planoId)) {
            if (modeloOrigemId.equals(tarefa.getModeloOrigem().getId())) {
                return tarefa;
            }
        }

        ModeloTarefa modelo = modeloTarefaRepository.findById(modeloOrigemId)
                .orElseThrow(() -> new RuntimeException("Modelo nao encontrado"));

        // Criação por modelo informado. A geração automática por curso depende
        // da associação com seus modelos, ainda ausente no projeto.
        TarefaPlano tarefa = plano.gerarTarefa(modelo);
        return tarefaPlanoRepository.save(tarefa);
    }

    public TarefaPlano iniciar(Long planoId, Long id) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.iniciar();
        return tarefaPlanoRepository.save(tarefa);
    }

    public TarefaPlano concluir(Long planoId, Long id) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.concluir();
        return tarefaPlanoRepository.save(tarefa);
    }

    public TarefaPlano reabrir(Long planoId, Long id) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.reabrir();
        return tarefaPlanoRepository.save(tarefa);
    }

    public TarefaPlano alterarPrazo(Long planoId, Long id, LocalDate prazo) {
        TarefaPlano tarefa = GetByid(planoId, id);
        tarefa.alterarPrazo(prazo);
        return tarefaPlanoRepository.save(tarefa);
    }
}
