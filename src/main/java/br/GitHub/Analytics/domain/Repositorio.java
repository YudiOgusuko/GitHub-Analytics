package br.GitHub.Analytics.domain;

import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.snapshot.resultadoRepositorio.ResultadoRepositorios;
import br.GitHub.Analytics.repository.IRepositorioRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

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
    @JsonIgnore
    private Long repositorioId;

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

    @ElementCollection
    @CollectionTable(
            name = "repositorio_topicos",
            joinColumns = @JoinColumn(name = "repositorio_id")
    )
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
        this.repositorioId = repositorioDto.id();
        this.nome = repositorioDto.nome();
        this.descricao = repositorioDto.descricao();
        this.criadoEm = repositorioDto.criadoEm();
        this.ultimoUpdate = repositorioDto.ultimoUpdate();
        this.estrelas = repositorioDto.estrelas();
        this.forks = repositorioDto.forks();
        this.linguagem = repositorioDto.linguagem();
        this.openIssues = repositorioDto.openIssues();
        this.topicos = repositorioDto.topicos();
    }

    public static ResultadoRepositorios temDadosDiferentes(Usuario usuario, List<RepositoriosMultiplosDto> repositorioDaApi, IRepositorioRepository repositorioRepository) {
        Map<Long, Repositorio> repositorioId = repositorioRepository.findAllByUsuarioId(usuario.getId()).stream()
                .collect(Collectors.toMap(Repositorio::getRepositorioId, r -> r));

        int atualizados = 0;
        int adicionados = 0;

        for (RepositoriosMultiplosDto dto : repositorioDaApi) {
            Repositorio existente = repositorioId.get(dto.id());
            if (existente != null) {
                if(existente.atualizarDados(dto)) atualizados++;
            } else {
                usuario.adicionarRepositorio(new Repositorio(dto));
                adicionados++;
            }
        }
        return new ResultadoRepositorios(adicionados, atualizados);
    }

    public boolean atualizarDados(RepositoriosMultiplosDto dto) {
        boolean mudou = !Objects.equals(this.estrelas, dto.estrelas())
                || !Objects.equals(this.forks, dto.forks())
                || !Objects.equals(this.descricao, dto.descricao())
                || !Objects.equals(this.linguagem, dto.linguagem())
                || !Objects.equals(this.openIssues, dto.openIssues())
                || !mesmosTopicos(this.topicos, dto.topicos());

        if (mudou) {
            this.repositorioId = dto.id();
            this.nome = dto.nome();
            this.descricao = dto.descricao();
            this.criadoEm = dto.criadoEm();
            this.ultimoUpdate = dto.ultimoUpdate();
            this.estrelas = dto.estrelas();
            this.forks = dto.forks();
            this.linguagem = dto.linguagem();
            this.openIssues = dto.openIssues();
            this.topicos = dto.topicos();
        }
        return mudou;
    }

    private static boolean mesmosTopicos(List<String> a, List<String> b) {
        return new HashSet<>(a == null ? List.of() : a)
                .equals(new HashSet<>(b == null ? List.of() : b));
    }
}
