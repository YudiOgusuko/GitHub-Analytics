package br.GitHub.Analytics.dto.repositorioDados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RepositorioDados(@JsonProperty("name") String nome,
                               @JsonProperty("html_url") String pagina,
                               @JsonProperty("description") String descricao,
                               @JsonProperty("created_at") Instant criadoEm,
                               @JsonProperty("updated_at") Instant ultimoUpdate,
                               @JsonProperty("stargazers_count") Integer estrelas,
                               @JsonProperty("language") String linguagem,
                               Integer forks,
                               @JsonProperty("open_issues") Integer openIssues) {

}
