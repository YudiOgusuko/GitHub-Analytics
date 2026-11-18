package br.GitHub.Analytics.dto.repositorio.contribuidores;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Contribuidores(@JsonProperty("login") String userName) {
}
