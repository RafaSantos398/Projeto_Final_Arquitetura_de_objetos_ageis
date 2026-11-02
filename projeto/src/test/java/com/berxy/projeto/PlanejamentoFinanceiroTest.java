package com.berxy.projeto;

import com.berxy.projeto.PlanoIntercambio.*;
import com.berxy.projeto.user.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlanejamentoFinanceiroTest {
    private PlanoIntercambio plano(String meta, String guardado, String aporte, LocalDate inicio) {
        var p = new PlanoIntercambio(new User("Teste", "teste@teste.com", "hash"), "Viagem", TipoIntercambio.CURSO_IDIOMA, StatusPlano.RASCUNHO);
        p.atualizarPlanejamento(null, "BRL", inicio, 6, false);
        p.atualizarFinancas(new BigDecimal(meta), new BigDecimal(guardado), new BigDecimal(aporte));
        return p;
    }
    @Test void arredondaAporteParaCimaEContaSomenteMesesCompletos() {
        var r = PlanejamentoFinanceiro.de(plano("1000", "0", "300", LocalDate.of(2027, 2, 2)), LocalDate.of(2026, 11, 2));
        assertEquals(3L, r.mesesAteInicio());
        assertEquals(new BigDecimal("333.34"), r.aporteNecessario());
        assertEquals(4L, r.mesesParaMeta());
        assertEquals("AJUSTAR_APORTE", r.situacao());
    }
    @Test void naoDividePorZeroENaoExibeSaldoNegativo() {
        var hoje = LocalDate.of(2026, 11, 2);
        var r = PlanejamentoFinanceiro.de(plano("100", "150", "0", hoje), hoje);
        assertEquals(0, r.valorRestante().signum());
        assertEquals(0L, r.mesesParaMeta());
        assertNull(r.aporteNecessario());
        assertEquals("META_ATINGIDA", r.situacao());
        var semPrazo = PlanejamentoFinanceiro.de(plano("100", "0", "0", null), hoje);
        assertNull(semPrazo.mesesParaMeta());
        assertNull(semPrazo.previsaoNoInicio());
        assertEquals("SEM_DATA", semPrazo.situacao());
    }
    @Test void diferenciaPrazoVencidoEAporteQueAtendeMeta() {
        var hoje = LocalDate.of(2026, 11, 2);
        assertEquals("SEM_MESES_DISPONIVEIS", PlanejamentoFinanceiro.de(plano("100", "0", "50", hoje.minusDays(1)), hoje).situacao());
        assertEquals("NO_CAMINHO", PlanejamentoFinanceiro.de(plano("100", "0", "50", hoje.plusMonths(2)), hoje).situacao());
        assertEquals("SEM_APORTE", PlanejamentoFinanceiro.de(plano("100", "0", "0", hoje.plusMonths(2)), hoje).situacao());
    }
}
