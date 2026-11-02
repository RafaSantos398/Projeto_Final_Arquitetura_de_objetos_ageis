package com.berxy.projeto.TarefaPlano;

import java.time.Instant;

public record AnotacoesTarefaResponse(Long tarefaId, String texto, Instant atualizadoEm) {
    public static AnotacoesTarefaResponse de(TarefaPlano tarefa) {
        return new AnotacoesTarefaResponse(tarefa.getId(), tarefa.getAnotacoes(), tarefa.getAnotacoesAtualizadasEm());
    }
}
