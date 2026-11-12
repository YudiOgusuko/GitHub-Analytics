package br.GitHub.Analytics.dto.repositorio.pulls;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserPull(@JsonProperty("login") String userName) {
}
