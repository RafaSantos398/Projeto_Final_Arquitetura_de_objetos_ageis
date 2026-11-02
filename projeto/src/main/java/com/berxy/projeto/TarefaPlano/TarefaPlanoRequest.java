package com.berxy.projeto.TarefaPlano;

import java.time.LocalDate;

public record TarefaPlanoRequest(Long modeloOrigemId) {
    public record AnotacoesRequest(String texto) { }
    public record StatusRequest(StatusTarefa status) { }
    public record ConteudoRequest(String titulo, String descricao,
            com.berxy.projeto.ModeloTarefa.CategoriaTarefa categoria, LocalDate prazo) { }
    public record TemasRequest(java.util.List<TemaChecklist> temas) { }
    public record PrazoRequest(LocalDate prazo) {
    }
}
