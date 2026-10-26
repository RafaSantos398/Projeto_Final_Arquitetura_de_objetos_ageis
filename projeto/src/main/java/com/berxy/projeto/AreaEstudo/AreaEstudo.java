package com.berxy.projeto.AreaEstudo;


import jakarta.persistence.*;

@Entity
@Table(name = "area_estudo")
public class AreaEstudo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

}
