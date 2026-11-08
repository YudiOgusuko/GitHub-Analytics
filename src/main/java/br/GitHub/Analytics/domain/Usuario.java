package br.GitHub.Analytics.domain;

import br.GitHub.Analytics.dto.UsuarioUnicoDto;
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

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String perfil;

    @Column(nullable = false)
    private String bio;

    @Column(nullable = false)
    private String linguagemPreferida;

    @Column(nullable = false)
    private Integer repositorios;

    @Column(nullable = false)
    private Integer estrelas;

    @Column(nullable = false)
    private Integer seguidores;

    @Column(nullable = false)
    private Integer seguindo;

    @Column(nullable = false)
    private String criadoEm;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Repositorio> repositoriosList = new ArrayList<>();

    public Usuario(UsuarioUnicoDto userDto) {
        this.userName = userDto.userName();
        this.nome = userDto.nome();
        this.perfil = userDto.perfil();
        this.bio = userDto.bio();
        this.linguagemPreferida = userDto.linguagemPreferida();
        this.repositorios = userDto.repositorios();
        this.estrelas = userDto.estrelas();
        this.seguidores = userDto.seguidores();
        this.seguindo = userDto.seguindo();
        this.criadoEm = userDto.criadoEm();
    }

    public void adicionarRepositorio(Repositorio repositorio) {
        repositoriosList.add(repositorio);
        repositorio.setUsuario(this);
    }
}
