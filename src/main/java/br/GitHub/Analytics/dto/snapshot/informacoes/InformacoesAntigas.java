package br.GitHub.Analytics.dto.snapshot.informacoes;

import br.GitHub.Analytics.domain.Snapshot;

public record InformacoesAntigas(String nome,
                                 String bio,
                                 Integer repositorios,
                                 Integer seguidores,
                                 Integer seguindo,
                                 Integer estrelas,
                                 Integer contribuicoes,
                                 Integer commits,
                                 Integer issues,
                                 Integer pullRequests,
                                 Integer repositoriosAdicionados,
                                 Integer repositoriosAtualizados,
                                 Integer repositoriosRemovidos,
                                 String alterouTabelaUsuarios,
                                 String alterouTabelaRepositorios,
                                 String sincronizadoEm) {

    public InformacoesAntigas(Snapshot snapshot) {
        this(snapshot.getNome(), snapshot.getBio(), snapshot.getRepositorios(),
                snapshot.getSeguidores(), snapshot.getSeguindo(), snapshot.getEstrelas(), snapshot.getContribuicoes(),
                snapshot.getCommits(), snapshot.getIssues(), snapshot.getPullRequests(), snapshot.getRepositoriosAdicionados(),
                snapshot.getRepositoriosAtualizados(), snapshot.getRepositoriosRemovidos(), snapshot.getAlterouTabelaUsuarios(),
                snapshot.getAlterouTabelaRepositorios(), snapshot.getSincronizadoEm());
    }
}
