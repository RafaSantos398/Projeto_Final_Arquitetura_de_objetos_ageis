package com.berxy.projeto.PlanoIntercambio;

import com.berxy.projeto.AreaEstudo.AreaEstudo;
import com.berxy.projeto.PreparacaoIdioma.PreparacaoIdioma;
import com.berxy.projeto.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Plano-Intercambista")
public class PlanoIntercambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "User-Id", nullable = false)
    private User user;


    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false)
    private TipoIntercambio tipoIntercambio;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal orcamentoTotal;

    @Column(nullable = false, length = 3)
    private String moeda;

    @Column(nullable = false)
    private LocalDate inicioPrevisto;

    @Column(nullable = false)
    private Integer duracaoMeses;

    @Column()
    private boolean dependeBolsa;

    @Column(nullable = false)
    private StatusPlano status;

    @ManyToMany()
    @JoinTable(
            name = "plano_area_interesse",
            joinColumns = @JoinColumn(name = "plano_id"),
            inverseJoinColumns = @JoinColumn(name = "area_estudo_id")
    )
    private Set<AreaEstudo> areaInteresse;

    @OneToMany(mappedBy = "planoIntercambio")
    private List<PreparacaoIdioma> preparacoesIdioma = new ArrayList<>();
}
