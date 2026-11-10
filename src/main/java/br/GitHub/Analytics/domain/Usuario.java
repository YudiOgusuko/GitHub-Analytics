package br.GitHub.Analytics.domain;

import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "tb_usuarios")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String avatar;

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
    private String criadoEm;

    @Column(nullable = false)
    private String linguagemPreferida;

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
    private Integer pullRequestsRevisados;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Repositorio> repositoriosList = new ArrayList<>();

    public Usuario(UsuarioUnicoDto userDto) {
        this.userName = userDto.userName();
        this.nome = userDto.nome();
        this.avatar = userDto.perfil();
        this.bio = userDto.bio();
        this.linguagemPreferida = userDto.linguagemPreferida();
        this.repositorios = userDto.repositorios();
        this.estrelas = userDto.estrelas();
        this.seguidores = userDto.seguidores();
        this.seguindo = userDto.seguindo();
        this.criadoEm = userDto.criadoEm();
        this.contribuicoes = userDto.contribuicoes();
        this.commits = userDto.commits();
        this.issues = userDto.issues();
        this.pullRequests = userDto.pullRequests();
        this.pullRequestsRevisados = userDto.pullRequestsRevisados();
    }

    public void adicionarRepositorio(Repositorio repositorio) {
        repositoriosList.add(repositorio);
        repositorio.setUsuario(this);
    }
}
