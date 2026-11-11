package br.GitHub.Analytics.domain;

import br.GitHub.Analytics.dto.repositorio.RepositorioUnicoDto;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "tb_repositorios")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Repositorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String criadoEm;

    @Column(nullable = false)
    private String ultimoUpdate;

    @Column(nullable = false)
    private Integer estrelas;

    @Column(nullable = false)
    private Integer forks;

    @Column(nullable = false)
    private String linguagem;

    @Transient
    @JsonIgnore
    private List<String> topicos = new ArrayList<>();

    @Column(nullable = false)
    private Integer openIssues;

    @Transient
    @JsonIgnore
    private String branches;

    @Transient
    @JsonIgnore
    private List<String> linguagens = new ArrayList<>();

    @Transient
    @JsonIgnore
    private String contribuidores;

    @Transient
    @JsonIgnore
    private String commits;

    @Transient
    @JsonIgnore
    private String issues;

    @Transient
    @JsonIgnore
    private String pulls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Repositorio(RepositoriosMultiplosDto repositorioDto) {
        this.nome = repositorioDto.nome();
        this.descricao = repositorioDto.descricao();
        this.forks = repositorioDto.forks();
        this.linguagem = repositorioDto.linguagem();
        this.linguagens = repositorioDto.linguagens();
        this.estrelas = repositorioDto.estrelas();
        this.openIssues = repositorioDto.openIssues();
        this.topicos = repositorioDto.topicos();
        this.ultimoUpdate = repositorioDto.ultimoUpdate();
        this.criadoEm = repositorioDto.criadoEm();
    }

    public Repositorio(RepositorioUnicoDto repositorioDto) {
        this.nome = repositorioDto.nome();
        this.descricao = repositorioDto.descricao();
        this.criadoEm = repositorioDto.criadoEm();
        this.ultimoUpdate = repositorioDto.ultimoUpdate();
        this.estrelas = repositorioDto.estrelas();
        this.forks = repositorioDto.forks();
        this.openIssues = repositorioDto.openIssues();
        this.branches = repositorioDto.branches();
        this.linguagens = repositorioDto.linguagens();
        this.contribuidores = repositorioDto.contribuidores();
        this.commits = repositorioDto.commits();
        this.issues = repositorioDto.issues();
        this.pulls = repositorioDto.pulls();
        this.topicos = repositorioDto.topicos();
    }
}
