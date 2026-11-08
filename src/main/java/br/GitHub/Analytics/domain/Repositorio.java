package br.GitHub.Analytics.domain;

import br.GitHub.Analytics.dto.RepositorioDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    @Column(nullable = false)
    private String pagina;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String criadaEm;

    @Column(nullable = false)
    private String ultimoUpdate;

    @Column(nullable = false)
    private Integer estrelas;

    @Column(nullable = false)
    private String linguagem;

    @Column(nullable = false)
    private Integer forks;

    @Column(nullable = false)
    private Integer openIssues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    public Repositorio(RepositorioDto repositorioDto) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

        this.nome = repositorioDto.nome();
        this.pagina = repositorioDto.pagina();
        this.descricao = repositorioDto.descricao();
        this.criadaEm = repositorioDto.criadaEm() != null ? formatter.format(repositorioDto.criadaEm().atZone(ZoneId.of("UTC"))) : "N/A";
        this.ultimoUpdate = repositorioDto.criadaEm() != null ? formatter.format(repositorioDto.ultimoUpdate().atZone(ZoneId.of("UTC"))) : "N/A";
        this.estrelas = repositorioDto.estrelas();
        this.linguagem = repositorioDto.linguagem();
        this.forks = repositorioDto.forks();
        this.openIssues = repositorioDto.openIssues();

    }
}
