package com.berxy.projeto.TarefaPlano;

import java.time.LocalDate;

public record TarefaPlanoRequest(Long modeloOrigemId) {
    public record PrazoRequest(LocalDate prazo) {
    }
}
