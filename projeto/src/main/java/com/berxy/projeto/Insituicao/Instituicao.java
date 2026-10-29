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

    protected Instituicao() { }

    public Instituicao(String nome, String descricao, String siteOficial) {
        atualizarConteudo(nome, descricao, siteOficial);
    }

    public void atualizarConteudo(String nome, String descricao, String siteOficial) {
        String novoNome = validarTexto(nome, "Nome", 100, true);
        String novaDescricao = validarTexto(descricao, "Descrição", 500, false);
        String novoSite = validarTexto(siteOficial, "Site oficial", 500, false);
        if (novoSite != null) {
            try {
                java.net.URI uri = java.net.URI.create(novoSite);
                if (uri.getHost() == null || !("https".equalsIgnoreCase(uri.getScheme())
                        || "http".equalsIgnoreCase(uri.getScheme()))) {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("O site oficial deve ser uma URL HTTP ou HTTPS válida.");
            }
        }
        this.nome = novoNome;
        this.descricao = novaDescricao;
        this.siteOficial = novoSite;
    }

    private static String validarTexto(String valor, String campo, int limite, boolean obrigatorio) {
        if (valor == null || valor.isBlank()) {
            if (obrigatorio) throw new IllegalArgumentException(campo + " não pode ficar vazio.");
            return null;
        }
        String texto = valor.strip();
        if (texto.length() > limite) {
            throw new IllegalArgumentException(campo + " deve ter no máximo " + limite + " caracteres.");
        }
        return texto;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getSiteOficial() { return siteOficial; }
}
