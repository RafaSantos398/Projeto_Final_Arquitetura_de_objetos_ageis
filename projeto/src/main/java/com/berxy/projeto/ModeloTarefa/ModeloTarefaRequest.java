package com.berxy.projeto.ModeloTarefa;

public record ModeloTarefaRequest(
        String titulo,
        String descricao,
        CategoriaTarefa categoria
) {
}