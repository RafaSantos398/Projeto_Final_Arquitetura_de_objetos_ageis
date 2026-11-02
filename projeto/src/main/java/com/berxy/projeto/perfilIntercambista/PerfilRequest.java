package com.berxy.projeto.perfilIntercambista;
import com.berxy.projeto.PreparacaoIdioma.NivelProficiencia;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record PerfilRequest(
        @NotNull @Past LocalDate nascimento,
        @NotEmpty Set<@NotBlank @Size(max = 100) String> nacionalidades,
        @NotBlank @Size(max = 100) String paisResidencia,
        @NotNull List<@NotNull @Valid ProficienciaRequest> proficienciaIdioma,
        @NotNull SituacaoEscolaridade situacaoEscolaridade,
        @NotNull NivelEscolaridade nivelEscolaridade) {
    public record ProficienciaRequest(
            @NotBlank @Pattern(regexp = "(?i)[a-z]{2}(-[a-z]{2})?") String idioma,
            @NotNull NivelProficiencia nivel) { }
}
