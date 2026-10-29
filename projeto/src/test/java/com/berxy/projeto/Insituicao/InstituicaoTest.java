package com.berxy.projeto.Insituicao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InstituicaoTest {
    @Test
    void normalizaDadosEPermiteCamposOpcionais() {
        Instituicao instituicao = new Instituicao(" Universidade ", "  ", null);
        assertEquals("Universidade", instituicao.getNome());
        assertNull(instituicao.getDescricao());
        assertNull(instituicao.getSiteOficial());
    }

    @Test
    void rejeitaNomeVazioELimiteExcedido() {
        assertThrows(IllegalArgumentException.class, () -> new Instituicao(" ", null, null));
        assertThrows(IllegalArgumentException.class, () -> new Instituicao("a".repeat(101), null, null));
    }

    @Test
    void atualizacaoInvalidaPreservaDadosAnteriores() {
        Instituicao instituicao = new Instituicao("Original", "Descrição", "https://example.org");
        assertThrows(IllegalArgumentException.class,
                () -> instituicao.atualizarConteudo("Alterada", "Outra", "javascript:alert(1)"));
        assertEquals("Original", instituicao.getNome());
        assertEquals("Descrição", instituicao.getDescricao());
        assertEquals("https://example.org", instituicao.getSiteOficial());
    }
}
