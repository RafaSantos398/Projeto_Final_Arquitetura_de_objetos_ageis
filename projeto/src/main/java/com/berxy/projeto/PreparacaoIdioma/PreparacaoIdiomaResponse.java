package com.berxy.projeto.PreparacaoIdioma;

import java.time.LocalDate;

public record PreparacaoIdiomaResponse(
        Long id,
        Long planoId,
        String idioma,
        NivelProficiencia nivelAlvo,
        LocalDate dataMeta,
        Integer minutosSemanaisPlanejados,
        StatusPreparacao status
) {
    public static PreparacaoIdiomaResponse de(PreparacaoIdioma preparacao) {
        return new PreparacaoIdiomaResponse(
                preparacao.getId(), preparacao.getPlanoIntercambio().getId(),
                preparacao.getIdioma(), preparacao.getNivelAlvo(), preparacao.getDataMeta(),
                preparacao.getMinutosSemanaisPlanejados(), preparacao.getStatus()
        );
    }
}
