package br.GitHub.Analytics.dto.repositorio.branches;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Branches(@JsonProperty("name") String nome,
                       String url) {}
