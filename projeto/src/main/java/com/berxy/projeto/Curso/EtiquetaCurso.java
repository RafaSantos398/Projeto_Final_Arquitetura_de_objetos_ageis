package com.berxy.projeto.Curso;

import com.berxy.projeto.ModeloTarefa.CategoriaTarefa;

public enum EtiquetaCurso {
    FINANCEIRO, LINGUISTICO, ACADEMICO, PROFISSIONAL, CULTURAL, OUTROS;

    public CategoriaTarefa categoriaTarefa() {
        if (this == FINANCEIRO) return CategoriaTarefa.FINANCEIRO;
        if (this == LINGUISTICO) return CategoriaTarefa.IDIOMA;
        if (this == ACADEMICO) return CategoriaTarefa.INSCRICAO;
        return CategoriaTarefa.OUTROS;
    }
}
