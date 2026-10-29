package com.berxy.projeto.PlanoIntercambio;

import com.berxy.projeto.AreaEstudo.AreaEstudo;
import com.berxy.projeto.Curso.Curso;
import com.berxy.projeto.PreparacaoIdioma.PreparacaoIdioma;
import com.berxy.projeto.TarefaPlano.TarefaPlano;
import com.berxy.projeto.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "plano_intercambio")
public class PlanoIntercambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIntercambio tipoIntercambio;

    @Column(precision = 14, scale = 2)
    private BigDecimal orcamentoTotal;

    @Column(length = 3)
    private String moeda;

    @Column
    private LocalDate inicioPrevisto;

    @Column
    private Integer duracaoMeses;

    @Column
    private Boolean dependeBolsa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPlano status;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToMany
    @JoinTable(
            name = "plano_area_interesse",
            joinColumns = @JoinColumn(name = "plano_id"),
            inverseJoinColumns = @JoinColumn(name = "area_estudo_id")
    )
    private Set<AreaEstudo> areasInteresse = new HashSet<>();

    @OneToMany(mappedBy = "planoIntercambio")
    private List<PreparacaoIdioma> preparacoesIdioma =
            new ArrayList<>();

    @OneToMany(mappedBy = "plano")
    private List<TarefaPlano> tarefas = new ArrayList<>();

    protected PlanoIntercambio() {
        // Exigido pela JPA.
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitulo() {
        return titulo;
    }

    public TipoIntercambio getTipoIntercambio() {
        return tipoIntercambio;
    }

    public BigDecimal getOrcamentoTotal() {
        return orcamentoTotal;
    }

    public String getMoeda() {
        return moeda;
    }

    public LocalDate getInicioPrevisto() {
        return inicioPrevisto;
    }

    public Integer getDuracaoMeses() {
        return duracaoMeses;
    }

    public Boolean getDependeBolsa() {
        return dependeBolsa;
    }

    public StatusPlano getStatus() {
        return status;
    }

    public Curso getCurso() {
        return curso;
    }

    public Set<AreaEstudo> getAreasInteresse() {
        return Set.copyOf(areasInteresse);
    }

    public List<PreparacaoIdioma> getPreparacoesIdioma() {
        return List.copyOf(preparacoesIdioma);
    }

    public List<TarefaPlano> getTarefas() {
        return List.copyOf(tarefas);
    }
}