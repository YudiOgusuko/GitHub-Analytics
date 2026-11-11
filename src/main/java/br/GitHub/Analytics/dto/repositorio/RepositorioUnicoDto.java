package br.GitHub.Analytics.dto.repositorio;

import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.dto.repositorio.dados.RepositorioDados;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@JsonPropertyOrder({
        "nome",
        "descricao",
        "linguagens",
        "commits",
        "branches",
        "pulls",
        "forks",
        "estrelas",
        "issues",
        "openIssues",
        "topicos",
        "contribuidores",
        "ultimoUpdate",
        "criadoEm"})
public record RepositorioUnicoDto(String nome,
                                  String descricao,
                                  String branches,
                                  List<String> linguagens,
                                  String contribuidores,
                                  String commits,
                                  String issues,
                                  String pulls,
                                  String criadoEm,
                                  String ultimoUpdate,
                                  Integer estrelas,
                                  Integer forks,
                                  List<String> topicos,
                                  Integer openIssues){


    public RepositorioUnicoDto(RepositorioDados dados) {
        this(dados.nome() != null ? dados.nome() : "N/A", dados.descricao() != null ? dados.descricao() : "N/A",
                dados.branches() != null ? dados.branches() : "N/A", pegarChaves(dados.linguagens()),
                dados.contribuidores() != null ? dados.contribuidores() : "N/A",
                dados.commits() != null ? dados.commits() : "N/A", dados.issues() != null ? dados.issues() : "N/A",
                dados.pulls() != null ? dados.pulls() : "N/A", formatarData(dados.criadoEm()), formatarData(dados.ultimoUpdate()),
                dados.estrelas() != null ? dados.estrelas() : 0, dados.forks() != null ? dados.forks() : 0,
                dados.topicos() != null ? dados.topicos() : List.of(), dados.openIssues() != null ? dados.openIssues() : 0);
    }

    public RepositorioUnicoDto(List<Repositorio> repositorio) {
        this(repositorio.get(0).getNome(), repositorio.get(0).getDescricao(), repositorio.get(0).getBranches(), repositorio.get(0).getLinguagens(),
                repositorio.get(0).getContribuidores(), repositorio.get(0).getCommits(), repositorio.get(0).getIssues(), repositorio.get(0).getPulls(),
                repositorio.get(0).getCriadoEm(), repositorio.get(0).getUltimoUpdate(), repositorio.get(0).getEstrelas(),
                repositorio.get(0).getForks(), repositorio.get(0).getTopicos(), repositorio.get(0).getOpenIssues());
    }

    private static List<String> pegarChaves (Map<String, Integer> map) {
        List<String> linguagens = new ArrayList<>();

        for(String chave : map.keySet()) {
            if(chave != null && !chave.equalsIgnoreCase("N/A")) {
                linguagens.add(chave);
            }
        }
        return linguagens;
    }

    private static String formatarData(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
        return formatter.format(instant.atZone(ZoneId.of("UTC")));
    }
}
