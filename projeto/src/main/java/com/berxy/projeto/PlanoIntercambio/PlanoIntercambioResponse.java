package com.berxy.projeto.PlanoIntercambio;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanoIntercambioResponse(
        Long id,
        String titulo,
        TipoIntercambio tipoIntercambio,
        StatusPlano status,
        BigDecimal orcamentoTotal,
        String moeda,
        LocalDate inicioPrevisto,
        Integer duracaoMeses,
        Boolean dependeBolsa,
        boolean cursoSelecionado
) {
    public static PlanoIntercambioResponse de(PlanoIntercambio plano) {
        return new PlanoIntercambioResponse(
                plano.getId(), plano.getTitulo(), plano.getTipoIntercambio(),
                plano.getStatus(), plano.getOrcamentoTotal(), plano.getMoeda(),
                plano.getInicioPrevisto(), plano.getDuracaoMeses(),
                plano.getDependeBolsa(), plano.getCurso() != null
        );
    }
}
