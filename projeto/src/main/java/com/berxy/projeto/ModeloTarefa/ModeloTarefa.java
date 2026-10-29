package com.berxy.projeto.ModeloTarefa;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "modelo_tarefa")
public class ModeloTarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 400)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaTarefa categoria;

    @Column(nullable = false)
    private boolean ativo;

    protected ModeloTarefa() {
        // Exigido pela JPA.
    }

    public ModeloTarefa(
            String titulo,
            String descricao,
            CategoriaTarefa categoria
    ) {
        atualizarConteudo(titulo, descricao, categoria);
        this.ativo = true;
    }

    public void atualizarConteudo(
            String titulo,
            String descricao,
            CategoriaTarefa categoria
    ) {
        String tituloValidado = validarTexto(titulo, "Título", 200);
        String descricaoValidada = validarTexto(
                descricao, "Descrição", 400
        );

        CategoriaTarefa categoriaValidada = Objects.requireNonNull(
                categoria, "A categoria é obrigatória."
        );

        // Altera os campos somente depois de validar todos.
        this.titulo = tituloValidado;
        this.descricao = descricaoValidada;
        this.categoria = categoriaValidada;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
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

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public CategoriaTarefa getCategoria() {
        return categoria;
    }

    public boolean isAtivo() {
        return ativo;
    }
}