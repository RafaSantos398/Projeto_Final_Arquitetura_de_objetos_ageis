package com.berxy.projeto.perfilIntercambista;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record PerfilResponse(Long id, LocalDate nascimento, Set<String> nacionalidades,
        String paisResidencia, List<PerfilRequest.ProficienciaRequest> proficienciaIdioma,
        SituacaoEscolaridade situacaoEscolaridade, NivelEscolaridade nivelEscolaridade) {
    public static PerfilResponse from(PerfilINtercambista perfil) {
        return new PerfilResponse(perfil.getId(), perfil.getNascimento(), Set.copyOf(perfil.getNacionalidades()),
                perfil.getPaisResidencia(), perfil.getProficienciaIdioma().stream()
                .map(p -> new PerfilRequest.ProficienciaRequest(p.getIdioma(), p.getNivel())).toList(),
                perfil.getSituacaoEscolaridade(), perfil.getNivelEscolaridade());
    }
}
