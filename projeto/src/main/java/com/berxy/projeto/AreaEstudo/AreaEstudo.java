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


    public void atualizarArea(
            String nome,
            String descricao
    ) {
        String nomeValidado = validarTexto(nome, "Nome", 100, true);
        String descricaoValidada = validarTexto(
                descricao, "Descrição", 500, false
        );


        // Altera os campos somente depois de validar todos.
        this.nome = nomeValidado;
        this.descricao = descricaoValidada;
    }

    // considera se for opcional ou obrigatório no true
    private static String validarTexto(String valor, String campo, int max, boolean obrigatorio) {
        if (valor == null || valor.isBlank()) {
            if (obrigatorio) {
                throw new IllegalArgumentException(campo + " não pode ficar vazio.");
            }
            return null;
        }
        String texto = valor.strip();
        if (texto.length() > max) {
            throw new IllegalArgumentException(
                    campo + " deve ter no máximo " + max + " caracteres.");
        }
        return texto;
    }


    public AreaEstudo(String nome, String descricao) {
        atualizarArea(nome, descricao);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public AreaEstudo() {
    }
}
