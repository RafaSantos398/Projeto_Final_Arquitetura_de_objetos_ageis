package com.berxy.projeto.perfilIntercambista;
import com.berxy.projeto.user.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "Perfil_Intercambio")
public class PerfilINtercambista {
    @Id private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false) private LocalDate nascimento;
    @ElementCollection
    @CollectionTable(name = "perfil_nacionalidades", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "nacionalidade", nullable = false, length = 100)
    private Set<String> nacionalidades = new LinkedHashSet<>();
    @Column(nullable = false, length = 100) private String paisResidencia;
    @ElementCollection
    @CollectionTable(name = "perfil_proficiencia_idioma", joinColumns = @JoinColumn(name = "perfil_id"))
    private List<ProficienciaIdioma> proficienciaIdioma = new ArrayList<>();
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private SituacaoEscolaridade situacaoEscolaridade;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private NivelEscolaridade nivelEscolaridade;

    protected PerfilINtercambista() { }
    public PerfilINtercambista(User user, PerfilRequest dados) {
        if (user == null) throw new IllegalArgumentException("Usuario obrigatorio");
        this.user = user;
        atualizar(dados);
    }
    public void atualizar(PerfilRequest dados) {
        if (dados == null || dados.nascimento() == null || !dados.nascimento().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Nascimento deve ser uma data passada");
        if (dados.nacionalidades() == null || dados.nacionalidades().isEmpty())
            throw new IllegalArgumentException("Informe ao menos uma nacionalidade");
        Set<String> novasNacionalidades = new LinkedHashSet<>();
        for (String n : dados.nacionalidades()) novasNacionalidades.add(texto(n));
        String novoPais = texto(dados.paisResidencia());
        if (dados.situacaoEscolaridade() == null || dados.nivelEscolaridade() == null)
            throw new IllegalArgumentException("Escolaridade obrigatoria");
        if (dados.proficienciaIdioma() == null) throw new IllegalArgumentException("Lista de proficiencias obrigatoria");
        List<ProficienciaIdioma> novas = new ArrayList<>();
        Set<String> idiomas = new HashSet<>();
        for (PerfilRequest.ProficienciaRequest p : dados.proficienciaIdioma()) {
            if (p == null) throw new IllegalArgumentException("Proficiencia invalida");
            ProficienciaIdioma proficiencia = new ProficienciaIdioma(p.idioma(), p.nivel());
            if (!idiomas.add(proficiencia.getIdioma())) throw new IllegalArgumentException("Idioma repetido no perfil");
            novas.add(proficiencia);
        }
        nascimento = dados.nascimento();
        nacionalidades.clear();
        nacionalidades.addAll(novasNacionalidades);
        paisResidencia = novoPais;
        proficienciaIdioma.clear();
        proficienciaIdioma.addAll(novas);
        situacaoEscolaridade = dados.situacaoEscolaridade();
        nivelEscolaridade = dados.nivelEscolaridade();
    }
    private static String texto(String valor) {
        if (valor == null || valor.isBlank() || valor.trim().length() > 100)
            throw new IllegalArgumentException("Pais e nacionalidades devem ter entre 1 e 100 caracteres");
        return valor.trim();
    }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public LocalDate getNascimento() { return nascimento; }
    public Set<String> getNacionalidades() { return Collections.unmodifiableSet(nacionalidades); }
    public String getPaisResidencia() { return paisResidencia; }
    public List<ProficienciaIdioma> getProficienciaIdioma() { return Collections.unmodifiableList(proficienciaIdioma); }
    public SituacaoEscolaridade getSituacaoEscolaridade() { return situacaoEscolaridade; }
    public NivelEscolaridade getNivelEscolaridade() { return nivelEscolaridade; }
}
