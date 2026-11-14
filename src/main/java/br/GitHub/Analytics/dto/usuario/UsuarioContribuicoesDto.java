package br.GitHub.Analytics.dto.usuario;

import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.usuario.contribuicoes.Contribuicoes;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
         "contribuicoes",
         "commits",
         "issues",
         "pullRequests",
})
public record UsuarioContribuicoesDto(Integer contribuicoes,
                                      Integer commits,
                                      Integer issues,
                                      Integer pullRequests) {

    public UsuarioContribuicoesDto(Contribuicoes contribuicoes) {
        this(contribuicoes.data().user().contributionsCollection() != null ? contribuicoes.data().user().contributionsCollection().contributionCalendar().totalContributions() : 0,
                contribuicoes.data().user().contributionsCollection().totalCommitContributions() != null ? contribuicoes.data().user().contributionsCollection().totalCommitContributions() : 0,
                contribuicoes.data().user().contributionsCollection().totalIssueContributions() != null ? contribuicoes.data().user().contributionsCollection().totalIssueContributions() : 0,
                contribuicoes.data().user().contributionsCollection().totalPullRequestContributions() != null ? contribuicoes.data().user().contributionsCollection().totalPullRequestContributions() : 0);
    }

    public UsuarioContribuicoesDto(Usuario usuario) {
        this(usuario.getContribuicoes(), usuario.getCommits(), usuario.getIssues(),
                usuario.getPullRequests());
    }
}
