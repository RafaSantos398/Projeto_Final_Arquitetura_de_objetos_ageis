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
        boolean ativo,
        boolean pessoal,
        java.util.List<EtiquetaCurso> etiquetas,
        java.time.LocalDate inicioPrevisto,
        java.time.LocalDate fimPrevisto,
        String link
) {
    public static CursoResponse de(Curso curso) {
        return new CursoResponse(
                curso.getId(), curso.getInstituicao() == null ? null : curso.getInstituicao().getId(), curso.getInstituicaoNome(),
                curso.getNome(), curso.getDescricao(), curso.getTipoIntercambio(),
                curso.getIdioma(), curso.getDuracaoMeses(), curso.isAtivo(), curso.isPessoal(),
                curso.getEtiquetas(), curso.getInicioPrevisto(), curso.getFimPrevisto(), curso.getLink()
        );
    }
}
