package br.GitHub.Analytics.dto.repositorio;

import br.GitHub.Analytics.dto.repositorio.branches.Branches;
import br.GitHub.Analytics.dto.repositorio.commits.Commits;
import br.GitHub.Analytics.dto.repositorio.contribuidores.Contribuidores;
import br.GitHub.Analytics.dto.repositorio.dados.RepositorioDados;
import br.GitHub.Analytics.dto.repositorio.pulls.Pulls;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({
        "nome",
        "descricao",
        "commits",
        "branches",
        "issues",
        "pulls",
        "forks",
        "estrelas",
        "linguagemPrincipal",
        "linguagensUsadas",
        "contribuidores",
        "topicos"})

public record RepositorioUnicoDto(String nome,
                                  String descricao,
                                  Integer commits,
                                  Integer branches,
                                  Integer issues,
                                  Integer pulls,
                                  Integer forks,
                                  Integer estrelas,
                                  String linguagemPrincipal,
                                  List<String> linguagensUsadas,
                                  List<Contribuidores> contribuidores,
                                  List<String> topicos){

    public RepositorioUnicoDto(List<RepositorioDados> dados, List<Branches> branches, Map<String, Integer> linguagens,  List<Contribuidores> contribuidores, List<Commits> commits, List<Pulls> pulls) {
        this(dados.get(0).nome() != null ? dados.get(0).nome() : "N/A", dados.get(0).descricao() != null ? dados.get(0).descricao() : "N/A",
            contarLista(commits), contarLista(branches), dados.get(0).openIssues() != null ? dados.get(0).openIssues() : 0, contarLista(pulls),
                dados.get(0).forks() != null ? dados.get(0).forks() : 0, dados.get(0).estrelas() != null ? dados.get(0).estrelas() : 0,
                dados.get(0).linguagem() != null ? dados.get(0).linguagem() : "N/A", pegarChaves(linguagens), contribuidores,
                dados.get(0).topicos() != null ? dados.get(0).topicos() : List.of());
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

    private static Integer contarLista (List<?> lista) {
        if (lista == null || lista.isEmpty()) {
            return 0;
        }

        return lista.size();
    }
}
