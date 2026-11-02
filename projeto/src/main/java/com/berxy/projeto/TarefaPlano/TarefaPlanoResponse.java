package com.berxy.projeto.TarefaPlano;

import java.time.Instant;
import java.time.LocalDate;

public record TarefaPlanoResponse(
        Long id,
        Long planoId,
        Long modeloOrigemId,
        String titulo,
        String descricao,
        StatusTarefa status,
        LocalDate prazo,
        Instant concluidaEm,
        com.berxy.projeto.ModeloTarefa.CategoriaTarefa categoria,
        TemaChecklist tema,
        boolean personalizada
) {
    public static TarefaPlanoResponse de(TarefaPlano tarefa) {
        return new TarefaPlanoResponse(
                tarefa.getId(), tarefa.getPlano().getId(),
                tarefa.getModeloOrigem() == null ? null : tarefa.getModeloOrigem().getId(),
                tarefa.getTitulo(), tarefa.getDescricao(), tarefa.getStatus(),
                tarefa.getPrazo(), tarefa.getConcluidaEm(), tarefa.getCategoria(), tarefa.getTema(),
                tarefa.getModeloOrigem() == null && tarefa.getTema() == null
        );
    }
}
