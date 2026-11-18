package br.GitHub.Analytics.dto.usuario.contribuicoes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContributionCalendar(Integer totalContributions) {
}
