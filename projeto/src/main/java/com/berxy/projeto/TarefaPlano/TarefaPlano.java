package com.berxy.projeto.TarefaPlano;

import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import com.berxy.projeto.ModeloTarefa.CategoriaTarefa;
import com.berxy.projeto.PlanoIntercambio.PlanoIntercambio;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "tarefa_plano",
        uniqueConstraints = {@UniqueConstraint(
                name = "uk_tarefa_plano_modelo",
                columnNames = {"plano_id", "modelo_origem_id"}
        ), @UniqueConstraint(name = "uk_tarefa_plano_sugestao", columnNames = {"plano_id", "chave_sugestao"})}
)
public class TarefaPlano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plano_id", nullable = false)
    private PlanoIntercambio plano;

    @ManyToOne
    @JoinColumn(name = "modelo_origem_id")
    private ModeloTarefa modeloOrigem;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private CategoriaTarefa categoria;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private TemaChecklist tema;

    @Column(length = 80)
    private String chaveSugestao;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 400)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTarefa status;

    @Column
    private LocalDate prazo;

    @Column
    private Instant concluidaEm;

    protected TarefaPlano() {
        // Exigido pela JPA.
    }

    public TarefaPlano(
            PlanoIntercambio plano,
            ModeloTarefa modeloOrigem
    ) {
        if (plano == null) {
            throw new IllegalArgumentException("O plano é obrigatório.");
        }

        if (modeloOrigem == null) {
            throw new IllegalArgumentException("O modelo de tarefa é obrigatório.");
        }

        if (!modeloOrigem.isAtivo()) {
            throw new IllegalArgumentException(
                    "Não é possível gerar uma tarefa de um modelo inativo."
            );
        }

        this.plano = plano;
        this.modeloOrigem = modeloOrigem;
        this.titulo = modeloOrigem.getTitulo();
        this.descricao = modeloOrigem.getDescricao();
        this.categoria = modeloOrigem.getCategoria();
        this.status = StatusTarefa.PENDENTE;

        // Mantém os dois lados da associação consistentes em memória.
        plano.adicionarTarefa(this);
    }

    public TarefaPlano(PlanoIntercambio plano, String titulo, String descricao,
                       CategoriaTarefa categoria, LocalDate prazo) {
        if (plano == null) { throw new IllegalArgumentException("O plano é obrigatório."); }
        this.plano = plano;
        this.status = StatusTarefa.PENDENTE;
        atualizarConteudo(titulo, descricao, categoria, prazo);
        plano.adicionarTarefa(this);
    }

    public static TarefaPlano sugerida(PlanoIntercambio plano, TemaChecklist tema,
                                       TemaChecklist.Sugestao sugestao) {
        TarefaPlano tarefa = new TarefaPlano(plano, sugestao.titulo(), sugestao.descricao(), tema.getCategoria(), null);
        tarefa.tema = tema;
        tarefa.chaveSugestao = tema.name() + ":" + sugestao.chave();
        return tarefa;
    }

    public void atualizarConteudo(String titulo, String descricao, CategoriaTarefa categoria, LocalDate prazo) {
        if (titulo == null || titulo.isBlank() || titulo.strip().length() > 200) {
            throw new IllegalArgumentException("Informe um título de até 200 caracteres.");
        }
        String texto = descricao == null ? "" : descricao.strip();
        if (texto.length() > 400) { throw new IllegalArgumentException("A descrição deve ter até 400 caracteres."); }
        if (categoria == null) { throw new IllegalArgumentException("A categoria é obrigatória."); }
        this.titulo = titulo.strip();
        this.descricao = texto;
        this.categoria = categoria;
        this.prazo = prazo;
    }

    public CategoriaTarefa getCategoria() {
        return categoria != null ? categoria : modeloOrigem != null ? modeloOrigem.getCategoria() : CategoriaTarefa.OUTROS;
    }
    public TemaChecklist getTema() { return tema; }
    public String getChaveSugestao() { return chaveSugestao; }

    public void iniciar() {
        if (status != StatusTarefa.PENDENTE) {
            throw new IllegalStateException(
                    "Somente tarefas pendentes podem ser iniciadas."
            );
        }

        this.status = StatusTarefa.EM_ANDAMENTO;
    }

    public void concluir() {
        if (status == StatusTarefa.CONCLUIDA) {
            return;
        }

        this.status = StatusTarefa.CONCLUIDA;
        this.concluidaEm = Instant.now();
    }

    public void reabrir() {
        if (status != StatusTarefa.CONCLUIDA) {
            throw new IllegalStateException(
                    "Somente tarefas concluídas podem ser reabertas."
            );
        }

        this.status = StatusTarefa.PENDENTE;
        this.concluidaEm = null;
    }

    public void alterarPrazo(LocalDate prazo) {
        // null permite deixar a tarefa sem prazo.
        this.prazo = prazo;
    }

    public Long getId() {
        return id;
    }

    public PlanoIntercambio getPlano() {
        return plano;
    }

    public ModeloTarefa getModeloOrigem() {
        return modeloOrigem;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public Instant getConcluidaEm() {
        return concluidaEm;
    }
}
