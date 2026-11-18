package br.GitHub.Analytics.dto.repositorio.commits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Commits(@JsonProperty("author") Autor autor) {
}
