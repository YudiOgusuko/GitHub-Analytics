package br.GitHub.Analytics.dto.usuario.eventos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepoDto(@JsonProperty("name") String nome) {
}
