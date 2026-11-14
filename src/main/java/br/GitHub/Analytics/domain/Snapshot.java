package br.GitHub.Analytics.domain;

import br.GitHub.Analytics.service.Utilitarios;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Table(name = "tb_snapshot")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Snapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String bio;

    @Column(nullable = false)
    private Integer repositorios;

    @Column(nullable = false)
    private Integer seguidores;

    @Column(nullable = false)
    private Integer seguindo;

    @Column(nullable = false)
    private Integer estrelas;

    @Column(nullable = false)
    private Integer contribuicoes;

    @Column(nullable = false)
    private Integer commits;

    @Column(nullable = false)
    private Integer issues;

    @Column(nullable = false)
    private Integer pullRequests;

    @Column(nullable = false)
    private String sincronizadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Snapshot(Usuario usuario) {
        this.userName = usuario.getUserName();
        this.nome = usuario.getNome();
        this.bio = usuario.getBio();
        this.repositorios = usuario.getRepositorios();
        this.seguidores = usuario.getSeguidores();
        this.seguindo = usuario.getSeguindo();
        this.estrelas = usuario.getEstrelas();
        this.contribuicoes = usuario.getContribuicoes();
        this.commits = usuario.getCommits();
        this.issues = usuario.getIssues();
        this.pullRequests = usuario.getPullRequests();
        this.sincronizadoEm = Utilitarios.formatarDataSincronia(Instant.now());
    }
}
