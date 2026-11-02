package com.berxy.projeto.PreparacaoIdioma;

import java.time.LocalDate;

public record PreparacaoIdiomaRequest(
        String idioma,
        NivelProficiencia nivelAlvo,
        LocalDate dataMeta,
        Integer minutosSemanaisPlanejados
) {
}
