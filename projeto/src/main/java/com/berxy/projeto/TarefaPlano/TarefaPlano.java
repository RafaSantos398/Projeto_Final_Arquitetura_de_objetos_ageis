package com.berxy.projeto.TarefaPlano;

import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import com.berxy.projeto.PlanoIntercambio.PlanoIntercambio;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "tarefa_plano",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tarefa_plano_modelo",
                columnNames = {"plano_id", "modelo_origem_id"}
        )
)
public class TarefaPlano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plano_id", nullable = false)
    private PlanoIntercambio plano;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modelo_origem_id", nullable = false)
    private ModeloTarefa modeloOrigem;

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
        this.plano = Objects.requireNonNull(
                plano, "O plano é obrigatório."
        );

        this.modeloOrigem = Objects.requireNonNull(
                modeloOrigem, "O modelo de tarefa é obrigatório."
        );

        if (!modeloOrigem.isAtivo()) {
            throw new IllegalArgumentException(
                    "Não é possível gerar uma tarefa de um modelo inativo."
            );
        }

        this.titulo = modeloOrigem.getTitulo();
        this.descricao = modeloOrigem.getDescricao();
        this.status = StatusTarefa.PENDENTE;
    }

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