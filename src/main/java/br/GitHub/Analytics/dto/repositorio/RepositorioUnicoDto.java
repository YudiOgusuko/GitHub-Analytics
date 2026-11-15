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
        "pullsRequests",
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
                                  Integer pullsRequests,
                                  Integer forks,
                                  Integer estrelas,
                                  String linguagemPrincipal,
                                  List<String> linguagensUsadas,
                                  List<Contribuidores> contribuidores,
                                  List<String> topicos){

    public RepositorioUnicoDto(RepositorioDados dados, List<Branches> branches, Map<String, Integer> linguagens,  List<Contribuidores> contribuidores, List<Commits> commits, List<Pulls> pulls) {
        this(dados.nome() != null ? dados.nome() : "N/A", dados.descricao() != null ? dados.descricao() : "N/A",
            contarLista(commits), contarLista(branches), dados.openIssues() != null ? dados.openIssues() : 0, contarLista(pulls),
                dados.forks() != null ? dados.forks() : 0, dados.estrelas() != null ? dados.estrelas() : 0,
                dados.linguagem() != null ? dados.linguagem() : "N/A", pegarChaves(linguagens), contribuidores,
                dados.topicos() != null ? dados.topicos() : List.of());
    }


    private static List<String> pegarChaves (Map<String, Integer> map) {
        List<String> linguagens = new ArrayList<>();

        for(String chave : map.keySet()) {
            if(chave != null && !chave.equalsIgnoreCase("N/A")) linguagens.add(chave);
        }
        return linguagens;
    }

    private static Integer contarLista (List<?> lista) {
        if (lista == null || lista.isEmpty()) return 0;
        return lista.size();
    }
}
