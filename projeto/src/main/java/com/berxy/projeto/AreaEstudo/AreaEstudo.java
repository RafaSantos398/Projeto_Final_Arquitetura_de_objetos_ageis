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
        String nomeValidado = validarTexto(nome, "Título", 200);
        String descricaoValidada = validarTexto(
                descricao, "Descrição", 400
        );


        // Altera os campos somente depois de validar todos.
        this.nome = nomeValidado;
        this.descricao = descricaoValidada;
    }

    private static String validarTexto(
            String valor,
            String campo,
            int tamanhoMaximo
    ) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " não pode ficar vazio."
            );
        }

        String texto = valor.strip();

        if (texto.length() > tamanhoMaximo) {
            throw new IllegalArgumentException(
                    campo + " deve ter no máximo "
                            + tamanhoMaximo + " caracteres."
            );
        }

        return texto;
    }


    public AreaEstudo(String nome, String descricao) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


}
