package br.GitHub.Analytics.dto.usuario;

import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.repositorio.RepositorioDto;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioUnicoDados;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonPropertyOrder({
         "userName",
         "nome",
         "perfil",
         "bio",
         "linguagemPreferida",
         "repositorios",
         "estrelas",
         "contribuicoes",
         "commits",
         "issues",
         "pullRequests",
         "pullRequestsRevisados",
         "seguidores",
         "seguindo",
         "criadoEm"})

public record UsuarioUnicoDto(String userName,
                              String nome,
                              String perfil,
                              String bio,
                              String linguagemPreferida,
                              Integer repositorios,
                              Integer estrelas,
                              Integer seguidores,
                              Integer seguindo,
                              String criadoEm,
                              Integer contribuicoes,
                              Integer commits,
                              Integer issues,
                              Integer pullRequests,
                              Integer pullRequestsRevisados) {

    public UsuarioUnicoDto(UsuarioUnicoDados dados, List<RepositorioDto> repositorioDto, UsuarioContribuicoesDto contribuicoesDto) {
        this(dados.userName() != null ? dados.userName() : "N/A", dados.nome() != null ? dados.nome() : "N/A",
                dados.avatar() != null ? dados.avatar() : "N/A", dados.bio() != null ? dados.bio() : "N/A",linguagemMaisUsada(repositorioDto),
                dados.totalRepositorios() != null ? dados.totalRepositorios() : 0, totalDeEstrelas(repositorioDto),
                dados.seguidores() != null ? dados.seguidores() : 0, dados.seguindo() != null ? dados.seguindo() : 0,
                formatarData(dados.criadoEm()), contribuicoesDto.contribuicoes(), contribuicoesDto.commits(), contribuicoesDto.issues(),
                contribuicoesDto.pullRequests(), contribuicoesDto.pullRequestsRevisados());
    }

    public UsuarioUnicoDto(Usuario usuario) {
        this(usuario.getUserName(), usuario.getNome(), usuario.getAvatar(), usuario.getBio(), usuario.getLinguagemPreferida(),
                usuario.getRepositorios(), usuario.getEstrelas(), usuario.getSeguidores(), usuario.getSeguindo(), usuario.getCriadoEm(),
                usuario.getContribuicoes(), usuario.getCommits(), usuario.getIssues(),
                usuario.getPullRequests(), usuario.getPullRequestsRevisados());
    }

    private static String linguagemMaisUsada (List<RepositorioDto> repositorioDto) {
        return repositorioDto.stream()
                .map(RepositorioDto::linguagem)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(l -> l, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Nenhuma linguagem encontrada.");
    }

    private static Integer totalDeEstrelas (List<RepositorioDto> repositorioDto) {
        return repositorioDto.stream().mapToInt(RepositorioDto::estrelas).sum();
    }

    private static String formatarData (Instant data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return formatter.format(data.atZone(ZoneId.of("UTC")));
    }
}
