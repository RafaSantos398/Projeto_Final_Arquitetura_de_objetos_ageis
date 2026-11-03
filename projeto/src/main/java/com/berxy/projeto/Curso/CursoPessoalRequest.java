package com.berxy.projeto.Curso;

import java.time.LocalDate;
import java.util.List;

public record CursoPessoalRequest(String nome, String descricao, String instituicaoNome,
        String idioma, LocalDate inicioPrevisto, LocalDate fimPrevisto, String link,
        List<EtiquetaCurso> etiquetas) { }
