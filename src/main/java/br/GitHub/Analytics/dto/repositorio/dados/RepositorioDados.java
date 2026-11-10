package br.GitHub.Analytics.dto.repositorio.dados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepositorioDados(@JsonProperty("name") String nome,
                               @JsonProperty("description") String descricao,
                               @JsonProperty("created_at") Instant criadoEm,
                               @JsonProperty("updated_at") Instant ultimoUpdate,
                               @JsonProperty("stargazers_count") Integer estrelas,
                               @JsonProperty("language") String linguagem,
                               Integer forks,
                               @JsonProperty("open_issues") Integer openIssues) {

}
