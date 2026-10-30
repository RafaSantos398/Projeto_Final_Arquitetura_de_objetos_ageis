package com.berxy.projeto.PlanoIntercambio;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanoIntercambioRequest(
        Long usuarioId,
        String titulo,
        TipoIntercambio tipoIntercambio,
        StatusPlano status,
        BigDecimal orcamentoTotal,
        String moeda,
        LocalDate inicioPrevisto,
        Integer duracaoMeses,
        Boolean dependeBolsa
) {
    // usuarioId é utilizado apenas no cadastro; a atualização não troca o dono.
    public record CursoRequest(Long cursoId) {
    }

    public record StatusRequest(StatusPlano status) {
    }
}
