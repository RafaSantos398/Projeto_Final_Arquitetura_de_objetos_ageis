package com.berxy.projeto.PreparacaoIdioma;


import com.berxy.projeto.PlanoIntercambio.PlanoIntercambio;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "preparacoes_idioma")
public class PreparacaoIdioma {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "PlanoIntercambio", nullable = false)
    private PlanoIntercambio planoIntercambio;

    @Column(nullable = false, length = 5)
    private String idioma;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelProficiencia nivelAlvo;


    @Column(nullable = false)
    private LocalDate dataMeta;


    @Column(nullable = false)
    private Integer minutosSemanaisPlanejados;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPreparacao status;


}



