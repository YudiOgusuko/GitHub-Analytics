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
        "forks",
        "linguagem",
        "estrelas",
        "openIssues",
        "topicos",
        "ultimoUpdate",
        "criadoEm"})

public record RepositoriosMultiplosDto(String nome,
                                       String descricao,
                                       String branchesUrl,
                                       List<String> linguagensUsadas,
                                       String contribuidoresUrl,
                                       String commitsUrl,
                                       String pullsUrl,
                                       String criadoEm,
                                       String ultimoUpdate,
                                       Integer estrelas,
                                       String linguagem,
                                       Integer forks,
                                       List<String> topicos,
                                       Integer openIssues){

    public RepositoriosMultiplosDto(RepositorioDados dados) {
        this(dados.nome() != null ? dados.nome() : "N/A", dados.descricao() != null ? dados.descricao() : "N/A",
                dados.branchesUrl() != null ? dados.branchesUrl() : "N/A",  List.of(dados.linguagensUrl()),
                dados.contribuidoresUrl() != null ? dados.contribuidoresUrl() : "N/A", dados.commitsUrl() != null ? dados.commitsUrl() : "N/A",
                dados.pullsUrl() != null ? dados.pullsUrl() : "N/A", formatarData(dados.criadoEm()), formatarData(dados.ultimoUpdate()),
                dados.estrelas() != null ? dados.estrelas() : 0, dados.linguagem() != null ? dados.linguagem() : "N/A",
                dados.forks() != null ? dados.forks() : 0,dados.topicos() != null ? dados.topicos() : List.of(), dados.openIssues() != null ? dados.openIssues() : 0);
    }

    public RepositoriosMultiplosDto(List<Repositorio> repositorio) {
        this(repositorio.get(0).getNome(), repositorio.get(0).getDescricao(), repositorio.get(0).getBranchesUrl(),
                repositorio.get(0).getLinguagensUsadas(), repositorio.get(0).getContribuidoresUrl(),
                repositorio.get(0).getCommitsUrl(), repositorio.get(0).getPullsUrl(), repositorio.get(0).getCriadoEm(),
                repositorio.get(0).getUltimoUpdate(), repositorio.get(0).getEstrelas(), repositorio.get(0).getLinguagem(),
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
