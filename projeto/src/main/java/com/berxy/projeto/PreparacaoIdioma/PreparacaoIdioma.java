package com.berxy.projeto.PreparacaoIdioma;

import com.berxy.projeto.PlanoIntercambio.PlanoIntercambio;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Locale;

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

    protected PreparacaoIdioma() {
        // Exigido pela JPA.
    }

    public PreparacaoIdioma(
            PlanoIntercambio planoIntercambio, String idioma,
            NivelProficiencia nivelAlvo, LocalDate dataMeta,
            Integer minutosSemanaisPlanejados
    ) {
        if (planoIntercambio == null) {
            throw new IllegalArgumentException("O plano é obrigatório.");
        }
        atualizarConteudo(idioma, nivelAlvo, dataMeta, minutosSemanaisPlanejados);
        this.planoIntercambio = planoIntercambio;
        this.status = StatusPreparacao.NAO_INICIADA;
    }

    public void atualizarConteudo(
            String idioma, NivelProficiencia nivelAlvo,
            LocalDate dataMeta, Integer minutosSemanaisPlanejados
    ) {
        String idiomaValidado = validarIdioma(idioma);
        if (nivelAlvo == null) {
            throw new IllegalArgumentException("O nível-alvo é obrigatório.");
        }
        if (dataMeta == null) {
            throw new IllegalArgumentException("A data-meta é obrigatória.");
        }
        if (minutosSemanaisPlanejados == null || minutosSemanaisPlanejados <= 0) {
            throw new IllegalArgumentException(
                    "Os minutos semanais planejados devem ser maiores que zero."
            );
        }

        // Valida todos os dados antes de alterar a preparação ou seu progresso.
        this.idioma = idiomaValidado;
        this.nivelAlvo = nivelAlvo;
        this.dataMeta = dataMeta;
        this.minutosSemanaisPlanejados = minutosSemanaisPlanejados;
    }

    public void iniciar() {
        if (status != StatusPreparacao.NAO_INICIADA) {
            throw new IllegalStateException("Somente preparações não iniciadas podem ser iniciadas.");
        }
        this.status = StatusPreparacao.EM_ANDAMENTO;
    }

    public void concluir() {
        this.status = StatusPreparacao.CONCLUIDA;
    }

    public boolean isConcluida() {
        return status == StatusPreparacao.CONCLUIDA;
    }

    private static String validarIdioma(String idioma) {
        if (idioma == null || !idioma.strip().matches("[a-zA-Z]{2}(-[a-zA-Z]{2})?")) {
            throw new IllegalArgumentException("O idioma deve ser um código como en ou pt-BR.");
        }
        String[] partes = idioma.strip().split("-");
        String codigo = partes[0].toLowerCase(Locale.ROOT);
        if (partes.length == 2) {
            codigo += "-" + partes[1].toUpperCase(Locale.ROOT);
        }
        return codigo;
    }

    public Long getId() {
        return id;
    }

    public PlanoIntercambio getPlanoIntercambio() {
        return planoIntercambio;
    }

    public String getIdioma() {
        return idioma;
    }

    public NivelProficiencia getNivelAlvo() {
        return nivelAlvo;
    }

    public LocalDate getDataMeta() {
        return dataMeta;
    }

    public Integer getMinutosSemanaisPlanejados() {
        return minutosSemanaisPlanejados;
    }

    public StatusPreparacao getStatus() {
        return status;
    }
}
