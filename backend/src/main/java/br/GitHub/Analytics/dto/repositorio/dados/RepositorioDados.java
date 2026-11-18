package br.GitHub.Analytics.dto.repositorio.dados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RepositorioDados(@JsonProperty("id") Long id,
                               @JsonProperty("name") String nome,
                               @JsonProperty("description") String descricao,
                               @JsonProperty("branches_url") String branchesUrl,
                               @JsonProperty("languages_url") String linguagensUrl,
                               @JsonProperty("contributors_url") String contribuidoresUrl,
                               @JsonProperty("commits_url") String commitsUrl,
                               @JsonProperty("pulls_url") String pullsUrl,
                               @JsonProperty("created_at") Instant criadoEm,
                               @JsonProperty("updated_at") Instant ultimoUpdate,
                               @JsonProperty("stargazers_count") Integer estrelas,
                               @JsonProperty("language") String linguagem,
                               @JsonProperty("forks_count") Integer forks,
                               @JsonProperty("topics") List<String> topicos,
                               @JsonProperty("open_issues") Integer openIssues) {

}
