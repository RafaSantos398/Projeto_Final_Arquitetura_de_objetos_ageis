package com.berxy.projeto.perfilIntercambista;

import com.berxy.projeto.PreparacaoIdioma.NivelProficiencia;
import jakarta.persistence.*;

@Embeddable
public class ProficienciaIdioma {

    @Column(nullable = false, length = 5)
    private String idioma;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelProficiencia nivel;

    // Exigido pelo JPA
    protected ProficienciaIdioma() {
    }

    public ProficienciaIdioma(String idioma, NivelProficiencia nivel) {
        if (idioma == null || !idioma.trim().matches("[a-zA-Z]{2}(-[a-zA-Z]{2})?"))
            throw new IllegalArgumentException("Idioma deve usar codigo como en ou pt-BR");
        if (nivel == null) throw new IllegalArgumentException("Nivel de proficiencia obrigatorio");
        this.idioma = idioma.trim().toLowerCase(java.util.Locale.ROOT);
        this.nivel = nivel;
    }

    public String getIdioma() {
        return idioma;
    }

    public NivelProficiencia getNivel() {
        return nivel;
    }
}
