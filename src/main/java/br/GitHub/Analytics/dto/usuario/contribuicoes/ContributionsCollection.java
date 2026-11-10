package br.GitHub.Analytics.dto.usuario.contribuicoes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContributionsCollection(ContributionCalendar contributionCalendar,
                                      Integer totalCommitContributions,
                                      Integer totalIssueContributions,
                                      Integer totalPullRequestContributions,
                                      Integer totalPullRequestReviewContributions) {
}
