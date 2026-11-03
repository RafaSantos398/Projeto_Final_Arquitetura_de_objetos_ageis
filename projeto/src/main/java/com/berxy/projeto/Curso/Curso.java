package com.berxy.projeto.Curso;

import com.berxy.projeto.Insituicao.Instituicao;
import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import com.berxy.projeto.PlanoIntercambio.TipoIntercambio;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "instituicao_id")
    private Instituicao instituicao;

    @ManyToOne
    @JoinColumn(name = "area_estudo_id")
    private com.berxy.projeto.AreaEstudo.AreaEstudo areaEstudo;

    public com.berxy.projeto.AreaEstudo.AreaEstudo getAreaEstudo() { return areaEstudo; }
    public void definirAreaEstudo(com.berxy.projeto.AreaEstudo.AreaEstudo area) { this.areaEstudo = area; }

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column
    private TipoIntercambio tipoIntercambio;

    // Idioma em que o curso é ministrado, por exemplo "en" ou "pt-BR".
    @Column(nullable = false, length = 5)
    private String idioma;

    @Column
    private Integer duracaoMeses;

    @Column(nullable = false)
    private boolean ativo;

    // Modelos usados para gerar o checklist de tarefas do plano que escolher este curso.
    @ManyToMany
    @JoinTable(
            name = "curso_modelo_tarefa",
            joinColumns = @JoinColumn(name = "curso_id"),
            inverseJoinColumns = @JoinColumn(name = "modelo_tarefa_id")
    )
    private Set<ModeloTarefa> modelosTarefa = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "proprietario_id")
    private com.berxy.projeto.user.User proprietario;

    @Column(length = 100)
    private String instituicaoInformada;
    @Column(length = 120)
    private String etiquetas;
    @Column
    private java.time.LocalDate inicioPrevisto;
    @Column
    private java.time.LocalDate fimPrevisto;
    @Column(length = 500)
    private String link;

    public static Curso pessoal(com.berxy.projeto.user.User dono, CursoPessoalRequest dados) {
        if (dono == null) throw new IllegalArgumentException("O proprietário é obrigatório.");
        Curso curso = new Curso();
        curso.proprietario = dono;
        curso.ativo = true;
        curso.atualizarPessoal(dados);
        return curso;
    }

    public void atualizarPessoal(CursoPessoalRequest dados) {
        if (proprietario == null) throw new IllegalStateException("Este curso pertence ao catálogo.");
        String novoNome = validarTexto(dados.nome(), "Nome", 100, true);
        String novaDescricao = validarTexto(dados.descricao(), "Descrição", 500, false);
        String novaInstituicao = validarTexto(dados.instituicaoNome(), "Instituição", 100, false);
        String novoIdioma = validarIdioma(dados.idioma());
        String novoLink = validarTexto(dados.link(), "Link", 500, false);
        if (novoLink != null) {
            try {
                java.net.URI uri = java.net.URI.create(novoLink);
                if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                        || uri.getHost() == null || uri.getUserInfo() != null) throw new IllegalArgumentException();
            } catch (IllegalArgumentException erro) {
                throw new IllegalArgumentException("Informe um link HTTP ou HTTPS válido.");
            }
        }
        if (dados.inicioPrevisto() != null && dados.fimPrevisto() != null
                && dados.fimPrevisto().isBefore(dados.inicioPrevisto())) {
            throw new IllegalArgumentException("O término não pode ser anterior ao início.");
        }
        if (dados.etiquetas() == null || dados.etiquetas().isEmpty()) {
            throw new IllegalArgumentException("Selecione pelo menos uma etiqueta.");
        }
        for (EtiquetaCurso etiqueta : dados.etiquetas()) {
            if (etiqueta == null) throw new IllegalArgumentException("Etiqueta inválida.");
        }
        java.util.List<String> nomes = new ArrayList<>();
        for (EtiquetaCurso etiqueta : EtiquetaCurso.values()) {
            if (dados.etiquetas().contains(etiqueta)) nomes.add(etiqueta.name());
        }
        this.nome = novoNome;
        this.descricao = novaDescricao;
        this.instituicaoInformada = novaInstituicao;
        this.idioma = novoIdioma;
        this.link = novoLink;
        this.inicioPrevisto = dados.inicioPrevisto();
        this.fimPrevisto = dados.fimPrevisto();
        this.etiquetas = String.join(",", nomes);
    }

    public com.berxy.projeto.user.User getProprietario() { return proprietario; }
    public boolean isPessoal() { return proprietario != null; }
    public String getInstituicaoNome() { return instituicao != null ? instituicao.getNome() : instituicaoInformada; }
    public java.time.LocalDate getInicioPrevisto() { return inicioPrevisto; }
    public java.time.LocalDate getFimPrevisto() { return fimPrevisto; }
    public String getLink() { return link; }
    public java.util.List<EtiquetaCurso> getEtiquetas() {
        java.util.List<EtiquetaCurso> resultado = new ArrayList<>();
        if (etiquetas != null && !etiquetas.isEmpty()) {
            for (String nome : etiquetas.split(",")) resultado.add(EtiquetaCurso.valueOf(nome));
        }
        return resultado;
    }

    protected Curso() {
        // Exigido pela JPA.
    }

    public Curso(
            Instituicao instituicao,
            String nome,
            String descricao,
            TipoIntercambio tipoIntercambio,
            String idioma,
            Integer duracaoMeses
    ) {
        atualizarConteudo(instituicao, nome, descricao, tipoIntercambio, idioma, duracaoMeses);
        this.ativo = true;
    }

    public void atualizarConteudo(
            Instituicao instituicao,
            String nome,
            String descricao,
            TipoIntercambio tipoIntercambio,
            String idioma,
            Integer duracaoMeses
    ) {
        if (instituicao == null) {
            throw new IllegalArgumentException("A instituição é obrigatória.");
        }

        String nomeValidado = validarTexto(nome, "Nome", 100, true);
        String descricaoValidada = validarTexto(descricao, "Descrição", 500, false);

        if (tipoIntercambio == null) {
            throw new IllegalArgumentException("O tipo de intercâmbio é obrigatório.");
        }

        String idiomaValidado = validarIdioma(idioma);

        if (duracaoMeses != null && duracaoMeses <= 0) {
            throw new IllegalArgumentException("A duração deve ser maior que zero.");
        }

        // Altera os campos somente depois de validar todos.
        this.instituicao = instituicao;
        this.nome = nomeValidado;
        this.descricao = descricaoValidada;
        this.tipoIntercambio = tipoIntercambio;
        this.idioma = idiomaValidado;
        this.duracaoMeses = duracaoMeses;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void adicionarModeloTarefa(ModeloTarefa modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("O modelo de tarefa é obrigatório.");
        }

        if (!modelo.isAtivo()) {
            throw new IllegalStateException(
                    "Não é possível associar um modelo de tarefa inativo."
            );
        }

        modelosTarefa.add(modelo);
    }

    public void removerModeloTarefa(ModeloTarefa modelo) {
        modelosTarefa.remove(modelo);
    }

    // Modelos desativados continuam associados, mas não geram novas tarefas.
    public List<ModeloTarefa> getModelosTarefaAtivos() {
        List<ModeloTarefa> ativos = new ArrayList<>();
        for (ModeloTarefa modelo : modelosTarefa) {
            if (modelo.isAtivo()) {
                ativos.add(modelo);
            }
        }
        return ativos;
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

    private static String validarIdioma(String idioma) {
        if (idioma == null || idioma.isBlank()) {
            throw new IllegalArgumentException("O idioma é obrigatório.");
        }

        if (!idioma.strip().matches("[a-zA-Z]{2}(-[a-zA-Z]{2})?")) {
            throw new IllegalArgumentException("O idioma deve ser um código como en ou pt-BR.");
        }
        String[] partes = idioma.strip().split("-");
        String codigo = partes[0].toLowerCase(Locale.ROOT);
        if (partes.length == 2) {
            codigo += "-" + partes[1].toUpperCase(Locale.ROOT);
        }

        if (!codigo.matches("[a-z]{2}(-[A-Z]{2})?")) {
            throw new IllegalArgumentException(
                    "O idioma deve ser um código como \"en\" ou \"pt-BR\"."
            );
        }

        return codigo;
    }

    public Long getId() { return id; }
    public Instituicao getInstituicao() { return instituicao; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public TipoIntercambio getTipoIntercambio() { return tipoIntercambio; }
    public String getIdioma() { return idioma; }
    public Integer getDuracaoMeses() { return duracaoMeses; }
    public boolean isAtivo() { return ativo; }
    public Set<ModeloTarefa> getModelosTarefa() { return Set.copyOf(modelosTarefa); }
}
