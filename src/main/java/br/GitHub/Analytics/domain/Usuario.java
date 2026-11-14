package br.GitHub.Analytics.domain;

import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import br.GitHub.Analytics.service.Utilitarios;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @JsonIgnore
    private String sincronizadoEm;

    @Transient
    @JsonIgnore
    private String avatar;

    @Transient
    @JsonIgnore
    private String seguidoresUrl;

    @Transient
    @JsonIgnore
    private String seguindoUrl;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Repositorio> repositoriosList = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Snapshot> snapshotList = new ArrayList<>();

    public Usuario(UsuarioUnicoDto userDto) {
        this.userName = userDto.userName();
        this.nome = userDto.nome();
        this.bio = userDto.bio();
        this.repositorios = userDto.repositorios();
        this.seguidores = userDto.seguidores();
        this.seguindo = userDto.seguindo();
        this.criadoEm = userDto.criadoEm();
        this.linguagemPreferida = userDto.linguagemPreferida();
        this.estrelas = userDto.estrelas();
        this.contribuicoes = userDto.contribuicoes();
        this.commits = userDto.commits();
        this.issues = userDto.issues();
        this.pullRequests = userDto.pullRequests();
        this.sincronizadoEm = Utilitarios.formatarDataSincronia(Instant.now());
    }

    public void adicionarRepositorio(Repositorio repositorio) {
        repositoriosList.add(repositorio);
        repositorio.setUsuario(this);
    }

    public void adicionarSnapshot(Snapshot snapshot) {
        snapshotList.add(snapshot);
        snapshot.setUsuario(this);
    }

    public void temDadosDiferentes(Usuario user, UsuarioUnicoDto userDto) {
        if(!Objects.equals(user.getUserName(), userDto.userName()) || !Objects.equals(user.getNome(), userDto.nome()) || !Objects.equals(user.bio, userDto.bio())
                || !Objects.equals(user.linguagemPreferida, userDto.linguagemPreferida()) || !Objects.equals(user.repositorios, userDto.repositorios())
                || !Objects.equals(user.estrelas, userDto.estrelas()) || !Objects.equals(user.seguidores, userDto.seguidores()) || !Objects.equals(user.seguindo, userDto.seguindo())
                || !Objects.equals(user.criadoEm, userDto.criadoEm()) || !Objects.equals(user.contribuicoes, userDto.contribuicoes()) || !Objects.equals(user.commits, userDto.commits())
                || !Objects.equals(user.issues, userDto.issues()) || !Objects.equals(user.pullRequests, userDto.pullRequests())) {

            user.atualizarDados(userDto);
        }
    }

    public void atualizarDados(UsuarioUnicoDto dto) {
        this.userName = dto.userName();
        this.nome = dto.nome();
        this.bio = dto.bio();
        this.linguagemPreferida = dto.linguagemPreferida();
        this.repositorios = dto.repositorios();
        this.estrelas = dto.estrelas();
        this.seguidores = dto.seguidores();
        this.seguindo = dto.seguindo();
        this.criadoEm = dto.criadoEm();
        this.contribuicoes = dto.contribuicoes();
        this.commits = dto.commits();
        this.issues = dto.issues();
        this.pullRequests = dto.pullRequests();
        this.sincronizadoEm = Utilitarios.formatarDataSincronia(Instant.now());
    }
}
