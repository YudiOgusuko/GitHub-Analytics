package br.GitHub.Analytics.dto;

import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.usuarioDados.UsuarioUnicoDados;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@JsonPropertyOrder({
        "userName",
        "nome",
        "perfil",
        "linguagemPreferida",
        "repositorios",
        "estrelas",
        "seguidores",
        "seguindo"})

public record UsuarioUnicoDto(String userName,
                              String nome,
                              String perfil,
                              String bio,
                              String linguagemPreferida,
                              Integer repositorios,
                              Integer estrelas,
                              Integer seguidores,
                              Integer seguindo,
                              String criadoEm) {

    public UsuarioUnicoDto(UsuarioUnicoDados dados, List<RepositorioDto> repositorioDto) {
        this(dados.userName() != null ? dados.userName() : "N/A", dados.nome() != null ? dados.nome() : "N/A",
                dados.perfil() != null ? dados.perfil() : "N/A", dados.bio() != null ? dados.perfil() : "N/A",linguagemMaisUsada(repositorioDto),
                dados.repositorios() != null ? dados.repositorios() : 0, totalDeEstrelas(repositorioDto),
                dados.seguidores() != null ? dados.seguidores() : 0, dados.seguindo() != null ? dados.seguindo() : 0,
                formatarData(dados.criadoEm()));
    }

    public UsuarioUnicoDto(Usuario usuario) {
        this(usuario.getUserName(), usuario.getNome(), usuario.getPerfil(), usuario.getBio(), usuario.getLinguagemPreferida(),
                usuario.getRepositorios(), usuario.getEstrelas(), usuario.getSeguidores(), usuario.getSeguindo(), usuario.getCriadoEm());
    }

    private static String linguagemMaisUsada (List<RepositorioDto> repositorioDto) {
        return repositorioDto.stream()
                .map(RepositorioDto::linguagem)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(l -> l, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Nenhuma linguagem encontrada");
    }

    private static Integer totalDeEstrelas (List<RepositorioDto> repositorioDto) {
        return repositorioDto.stream().mapToInt(RepositorioDto::estrelas).sum();
    }

    private static String formatarData (Instant data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return formatter.format(data.atZone(ZoneId.of("UTC")));
    }
}
