package com.berxy.projeto.PlanoIntercambio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record PlanejamentoFinanceiro(
        BigDecimal metaTotal, String moeda, BigDecimal valorGuardado, BigDecimal aporteMensal,
        BigDecimal valorRestante, Long mesesAteInicio, BigDecimal aporteNecessario,
        Long mesesParaMeta, BigDecimal previsaoNoInicio, String situacao) {

    public record Request(BigDecimal metaTotal, BigDecimal valorGuardado, BigDecimal aporteMensal) { }

    public static PlanejamentoFinanceiro de(PlanoIntercambio plano, LocalDate hoje) {
        BigDecimal meta = plano.getOrcamentoTotal();
        BigDecimal guardado = plano.getValorGuardado();
        BigDecimal mensal = plano.getAporteMensal();
        BigDecimal restante = meta == null ? null : meta.subtract(guardado).max(BigDecimal.ZERO);
        Long meses = plano.getInicioPrevisto() == null ? null
                : Math.max(0, ChronoUnit.MONTHS.between(hoje, plano.getInicioPrevisto()));
        BigDecimal necessario = restante == null || meses == null || meses == 0 ? null
                : restante.divide(BigDecimal.valueOf(meses), 2, RoundingMode.CEILING);
        Long mesesParaMeta = null;
        if (restante != null && restante.signum() == 0) {
            mesesParaMeta = 0L;
        } else if (restante != null && mensal.signum() > 0) {
            mesesParaMeta = restante.divide(mensal, 0, RoundingMode.CEILING).longValueExact();
        }
        BigDecimal previsao = meses == null ? null : guardado.add(mensal.multiply(BigDecimal.valueOf(meses)));
        String situacao = meta == null ? "SEM_META" : restante.signum() == 0 ? "META_ATINGIDA"
                : meses == null ? "SEM_DATA" : meses == 0 ? "SEM_MESES_DISPONIVEIS"
                : mensal.signum() == 0 ? "SEM_APORTE" : previsao.compareTo(meta) >= 0 ? "NO_CAMINHO" : "AJUSTAR_APORTE";
        return new PlanejamentoFinanceiro(meta, plano.getMoeda() == null ? "BRL" : plano.getMoeda(),
                guardado, mensal, restante, meses, necessario, mesesParaMeta, previsao, situacao);
    }
}
