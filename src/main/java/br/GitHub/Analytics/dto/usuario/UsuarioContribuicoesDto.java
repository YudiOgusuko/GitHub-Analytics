package br.GitHub.Analytics.dto.usuario;

import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.usuario.contribuicoes.Contribuicoes;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
         "contribuicoesTotais",
         "commitsTotais",
         "issuesTotais",
         "pullRequestsTotais",
         "pullRequestsRevisadosTotais"
})
public record UsuarioContribuicoesDto(Integer contribuicoesTotais,
                                      Integer commitsTotais,
                                      Integer issuesTotais,
                                      Integer pullRequestsTotais,
                                      Integer pullRequestsRevisadosTotais) {

    public UsuarioContribuicoesDto(Contribuicoes contribuicoes) {
        this(contribuicoes.data().user().contributionsCollection() != null ? contribuicoes.data().user().contributionsCollection().contributionCalendar().totalContributions() : 0,
                contribuicoes.data().user().contributionsCollection().totalCommitContributions() != null ? contribuicoes.data().user().contributionsCollection().totalCommitContributions() : 0,
                contribuicoes.data().user().contributionsCollection().totalIssueContributions() != null ? contribuicoes.data().user().contributionsCollection().totalIssueContributions() : 0,
                contribuicoes.data().user().contributionsCollection().totalPullRequestContributions() != null ? contribuicoes.data().user().contributionsCollection().totalPullRequestContributions() : 0,
                contribuicoes.data().user().contributionsCollection().totalPullRequestReviewContributions() != null ? contribuicoes.data().user().contributionsCollection().totalPullRequestReviewContributions() : 0);
    }

    public UsuarioContribuicoesDto(Usuario usuario) {
        this(usuario.getContribuicoes(), usuario.getCommits(), usuario.getIssues(),
                usuario.getPullRequests(), usuario.getPullRequestsRevisados());
    }
}
