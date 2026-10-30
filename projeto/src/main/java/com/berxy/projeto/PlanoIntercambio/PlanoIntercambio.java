package com.berxy.projeto.PlanoIntercambio;

import com.berxy.projeto.AreaEstudo.AreaEstudo;
import com.berxy.projeto.Curso.Curso;
import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import com.berxy.projeto.PreparacaoIdioma.PreparacaoIdioma;
import com.berxy.projeto.TarefaPlano.TarefaPlano;
import com.berxy.projeto.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

    public PlanoIntercambio(
            User user,
            String titulo,
            TipoIntercambio tipoIntercambio,
            StatusPlano statusInicial
    ) {
        if (user == null) {
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }

        this.user = user;
        atualizarIdentificacao(titulo, tipoIntercambio);
        alterarStatus(statusInicial);
    }

    public void atualizarIdentificacao(
            String titulo,
            TipoIntercambio tipoIntercambio
    ) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título é obrigatório.");
        }

        String tituloValidado = titulo.strip();
        if (tituloValidado.length() > 100) {
            throw new IllegalArgumentException(
                    "O título deve ter no máximo 100 caracteres."
            );
        }

        if (tipoIntercambio == null) {
            throw new IllegalArgumentException(
                    "O tipo de intercâmbio é obrigatório."
            );
        }

        this.titulo = tituloValidado;
        this.tipoIntercambio = tipoIntercambio;
    }

    public void atualizarPlanejamento(
            BigDecimal orcamentoTotal,
            String moeda,
            LocalDate inicioPrevisto,
            Integer duracaoMeses,
            Boolean dependeBolsa
    ) {
        BigDecimal orcamentoValidado = validarOrcamento(orcamentoTotal);
        String moedaValidada = validarMoeda(moeda);

        if (duracaoMeses != null && duracaoMeses <= 0) {
            throw new IllegalArgumentException(
                    "A duração deve ser maior que zero."
            );
        }

        // Os campos opcionais podem continuar nulos enquanto o plano é preparado.
        this.orcamentoTotal = orcamentoValidado;
        this.moeda = moedaValidada;
        this.inicioPrevisto = inicioPrevisto;
        this.duracaoMeses = duracaoMeses;
        this.dependeBolsa = dependeBolsa;
    }

    public void alterarStatus(StatusPlano status) {
        if (status == null) {
            throw new IllegalArgumentException("O status é obrigatório.");
        }

        this.status = status;
    }

    public void selecionarCurso(Curso curso) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso é obrigatório.");
        }

        if (this.curso == curso) {
            return;
        }

        if (!tarefas.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível trocar o curso após gerar tarefas."
            );
        }

        this.curso = curso;
    }

    public void adicionarAreaInteresse(AreaEstudo area) {
        if (area == null) {
            throw new IllegalArgumentException("A área de interesse é obrigatória.");
        }

        areasInteresse.add(area);
    }

    public void removerAreaInteresse(AreaEstudo area) {
        areasInteresse.remove(area);
    }

    public TarefaPlano gerarTarefa(ModeloTarefa modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("O modelo de tarefa é obrigatório.");
        }

        if (curso == null) {
            throw new IllegalStateException(
                    "Selecione um curso antes de gerar tarefas."
            );
        }

        TarefaPlano existente = buscarTarefaDoModelo(modelo);
        if (existente != null) {
            return existente;
        }

        // O service deve fornecer um modelo aplicável ao curso escolhido.
        return new TarefaPlano(this, modelo);
    }

    public void adicionarTarefa(TarefaPlano tarefa) {
        if (tarefa == null || tarefa.getPlano() != this) {
            throw new IllegalArgumentException(
                    "A tarefa deve pertencer a este plano."
            );
        }

        if (curso == null) {
            throw new IllegalStateException(
                    "Selecione um curso antes de adicionar tarefas."
            );
        }

        if (tarefas.contains(tarefa)) {
            return;
        }

        if (buscarTarefaDoModelo(tarefa.getModeloOrigem()) != null) {
            throw new IllegalStateException(
                    "Já existe uma tarefa deste modelo no plano."
            );
        }

        tarefas.add(tarefa);
    }

    private TarefaPlano buscarTarefaDoModelo(ModeloTarefa modelo) {
        for (TarefaPlano tarefa : tarefas) {
            ModeloTarefa origem = tarefa.getModeloOrigem();
            if (origem == modelo || (modelo.getId() != null
                    && modelo.getId().equals(origem.getId()))) {
                return tarefa;
            }
        }

        return null;
    }

    private static BigDecimal validarOrcamento(BigDecimal valor) {
        if (valor == null) {
            return null;
        }

        if (valor.signum() < 0) {
            throw new IllegalArgumentException("O orçamento não pode ser negativo.");
        }

        BigDecimal valorValidado;
        try {
            valorValidado = valor.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "O orçamento deve ter no máximo duas casas decimais.", exception
            );
        }

        if (valorValidado.precision() > 14) {
            throw new IllegalArgumentException(
                    "O orçamento excede o limite de 12 dígitos inteiros."
            );
        }

        return valorValidado;
    }

    private static String validarMoeda(String moeda) {
        if (moeda == null) {
            return null;
        }

        String moedaValidada = moeda.strip().toUpperCase(Locale.ROOT);
        if (!moedaValidada.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "A moeda deve ser um código de três letras."
            );
        }

        return moedaValidada;
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
