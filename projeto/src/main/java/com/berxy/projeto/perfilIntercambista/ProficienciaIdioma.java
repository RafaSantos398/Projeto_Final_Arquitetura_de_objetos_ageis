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
        this.idioma = idioma;
        this.nivel = nivel;
    }

    public String getIdioma() {
        return idioma;
    }

    public NivelProficiencia getNivel() {
        return nivel;
    }
}
