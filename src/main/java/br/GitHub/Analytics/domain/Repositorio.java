package br.GitHub.Analytics.domain;

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

    @Column(nullable = false)
    private Integer openIssues;

    @Transient
    @JsonIgnore
    private List<String> topicos = new ArrayList<>();

    @Transient
    @JsonIgnore
    private String branchesUrl;

    @Transient
    @JsonIgnore
    private List<String> linguagensUsadas = new ArrayList<>();

    @Transient
    @JsonIgnore
    private String contribuidoresUrl;

    @Transient
    @JsonIgnore
    private String commitsUrl;

    @Transient
    @JsonIgnore
    private String pullsUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Repositorio(RepositoriosMultiplosDto repositorioDto) {
        this.nome = repositorioDto.nome();
        this.descricao = repositorioDto.descricao();
        this.criadoEm = repositorioDto.criadoEm();
        this.ultimoUpdate = repositorioDto.ultimoUpdate();
        this.estrelas = repositorioDto.estrelas();
        this.forks = repositorioDto.forks();
        this.linguagem = repositorioDto.linguagem();
        this.openIssues = repositorioDto.openIssues();
        this.topicos = repositorioDto.topicos();
        this.branchesUrl = repositorioDto.branchesUrl();
        this.linguagensUsadas = repositorioDto.linguagensUsadas();
        this.contribuidoresUrl = repositorioDto.contribuidoresUrl();
        this.commitsUrl = repositorioDto.commitsUrl();
        this.pullsUrl = repositorioDto.pullsUrl();
    }
}
