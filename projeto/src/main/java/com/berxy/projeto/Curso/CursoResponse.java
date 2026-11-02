package com.berxy.projeto.Curso;

import com.berxy.projeto.PlanoIntercambio.TipoIntercambio;

public record CursoResponse(
        Long id,
        Long instituicaoId,
        String instituicaoNome,
        String nome,
        String descricao,
        TipoIntercambio tipoIntercambio,
        String idioma,
        Integer duracaoMeses,
        boolean ativo
) {
    public static CursoResponse de(Curso curso) {
        return new CursoResponse(
                curso.getId(), curso.getInstituicao().getId(), curso.getInstituicao().getNome(),
                curso.getNome(), curso.getDescricao(), curso.getTipoIntercambio(),
                curso.getIdioma(), curso.getDuracaoMeses(), curso.isAtivo()
        );
    }
}
