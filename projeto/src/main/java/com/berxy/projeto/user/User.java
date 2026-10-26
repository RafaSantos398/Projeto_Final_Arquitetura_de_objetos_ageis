package com.berxy.projeto.user;

import jakarta.persistence.*;

import java.time.Instant;

@Entity //faz a classe ser gerenciada pelo
@Table(name = "users")
public class User {


    @Id //mapeia o atributo para uma chave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Define como o identificador é gerado
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;


    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, name="passowrd_hash")
    private String passowrdHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt
            ;
    @Column(nullable = false)
    private Instant updatedAt;


    @PrePersist
    private void beforeInsert(){

        Instant now = Instant.now();

        this.updatedAt= now;
        this.createdAt = now;

        if (this.role == null){
            this.role = Role.USER;
        }

        this.active = true;
    }

    @PreUpdate
    private void beforeUpdate(){
        this.updatedAt = Instant.now();
    }
}
