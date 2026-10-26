package com.berxy.projeto.Insituicao;


import jakarta.persistence.*;

@Entity
@Table(name = "Insituicao")
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(length = 500)
    private String siteOficial;
}
