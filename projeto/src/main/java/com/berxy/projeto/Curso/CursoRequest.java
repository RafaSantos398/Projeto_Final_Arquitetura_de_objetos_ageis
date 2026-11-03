package com.berxy.projeto.Curso;

import com.berxy.projeto.PlanoIntercambio.TipoIntercambio;

public record CursoRequest(
        Long instituicaoId,
        String nome,
        String descricao,
        TipoIntercambio tipoIntercambio,
        String idioma,
        Integer duracaoMeses,
        Long areaEstudoId,
        String areaEstudoNome
) {
}
