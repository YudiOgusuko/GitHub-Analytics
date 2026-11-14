package br.GitHub.Analytics.dto.usuario;

import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioUnicoDados;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonPropertyOrder({
         "userName",
         "nome",
         "bio",
         "linguagemPreferida",
         "repositorios",
         "estrelas",
         "contribuicoes",
         "commits",
         "issues",
         "pullRequests",
         "seguidores",
         "seguindo",
         "criadoEm"})

public record UsuarioUnicoDto(String userName,
                              String nome,
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
                              Integer pullRequests) {

    public UsuarioUnicoDto(UsuarioUnicoDados dados, List<RepositoriosMultiplosDto> repositorioDto, UsuarioContribuicoesDto contribuicoesDto) {
        this(dados.userName() != null ? dados.userName() : "N/A", dados.nome() != null ? dados.nome() : "N/A",
                dados.bio() != null ? dados.bio() : "N/A",linguagemMaisUsada(repositorioDto),
                dados.totalRepositorios() != null ? dados.totalRepositorios() : 0, totalDeEstrelas(repositorioDto),
                dados.seguidores() != null ? dados.seguidores() : 0, dados.seguindo() != null ? dados.seguindo() : 0,
                formatarData(dados.criadoEm()), contribuicoesDto.contribuicoes(), contribuicoesDto.commits(), contribuicoesDto.issues(),
                contribuicoesDto.pullRequests());
    }

    public UsuarioUnicoDto(Usuario usuario) {
        this(usuario.getUserName(), usuario.getNome(), usuario.getBio(), usuario.getLinguagemPreferida(), usuario.getRepositorios(),
                usuario.getEstrelas(), usuario.getSeguidores(), usuario.getSeguindo(), usuario.getCriadoEm(), usuario.getContribuicoes(),
                usuario.getCommits(), usuario.getIssues(), usuario.getPullRequests());
    }

    private static String linguagemMaisUsada (List<RepositoriosMultiplosDto> linguagens) {
        if (linguagens == null || linguagens.isEmpty()) {
            return "Nenhuma linguagem encontrada.";
        }

        return linguagens.stream()
                .map(RepositoriosMultiplosDto::linguagem)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Nenhuma linguagem encontrada.");
    }

    private static Integer totalDeEstrelas (List<RepositoriosMultiplosDto> repositorioDto) {
        return repositorioDto.stream().mapToInt(RepositoriosMultiplosDto::estrelas).sum();
    }

    private static String formatarData(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("pt-BR"));
        return formatter.format(instant.atZone(ZoneId.of("UTC")));
    }
}
