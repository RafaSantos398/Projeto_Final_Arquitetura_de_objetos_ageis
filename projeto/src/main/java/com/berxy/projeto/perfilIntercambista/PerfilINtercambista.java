package com.berxy.projeto.perfilIntercambista;
import com.berxy.projeto.user.User;


import jakarta.persistence.*;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.Set;
import  java.util.List;

@Entity
@Table(name =  "Perfil_Intercambio")
public class PerfilINtercambista {


    @Id//Identificador do perfil
    private long id;


    @OneToOne(optional = false)//Faz a relação 1 pra 1
    @MapsId//Faz o perfil usar o identificador do usuário associado, basicamente obriga essa classe a usar o id do user
    @JoinColumn(name = "user_id", nullable = false)//Define a coluna que referencia o usuário no banco
    private User user;


    @Column(nullable = false)
    private LocalDate nascimento;

    @ElementCollection
    @Column(name = "naciocnalidade" ,nullable = false)
    private Set<String> nacionalidades;

    @Column(nullable = false )
    private String paisResidencia;

    @Column(nullable = false)
    private List<ProficienciaIdioma> proficienciaIdioma;

    @Column(nullable = false)
    private SituacaoEscolaridade situacaoEscolaridade;

    @Column(nullable = false)
    private NivelEscolaridade nivelEscolaridade;


    public List<ProficienciaIdioma> getProficienciaIdioma() {
        return proficienciaIdioma;
    }

    public NivelEscolaridade getNivelEscolaridade() {
        return nivelEscolaridade;
    }

    public long getId() {
        return id;
    }

    public void setNacionalidades(Set<String> nacionalidades) {
        this.nacionalidades = nacionalidades;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    public void setNivelEscolaridade(NivelEscolaridade nivelEscolaridade) {
        this.nivelEscolaridade = nivelEscolaridade;
    }

    public Set<String> getNacionalidades() {
        return nacionalidades;
    }

    public SituacaoEscolaridade getSituacaoEscolaridade() {
        return situacaoEscolaridade;
    }

    public String getPaisResidencia() {
        return paisResidencia;
    }

    public User getUser() {
        return user;
    }

    public void setNascimento(LocalDate nascimento) {
        this.nascimento = nascimento;
    }
    public void setPaisResidencia(String paisResidencia) {
        this.paisResidencia = paisResidencia;
    }

    public void setProficienciaIdioma(List<ProficienciaIdioma> proficienciaIdioma) {
        this.proficienciaIdioma = proficienciaIdioma;
    }
    public void setSituacaoEscolaridade(SituacaoEscolaridade situacaoEscolaridade) {
        this.situacaoEscolaridade = situacaoEscolaridade;
    }

}
