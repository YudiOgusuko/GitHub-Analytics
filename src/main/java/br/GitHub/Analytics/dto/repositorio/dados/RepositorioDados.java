package br.GitHub.Analytics.dto.repositorio.dados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepositorioDados(@JsonProperty("name") String nome,
                               @JsonProperty("description") String descricao,
                               @JsonProperty("branches_url") String branches,
                               @JsonProperty("languages_url") Map<String, Integer> linguagens,
                               @JsonProperty("contributors_url") String contribuidores,
                               @JsonProperty("commits_url") String commits,
                               @JsonProperty("issues_url") String issues,
                               @JsonProperty("pulls_url") String pulls,
                               @JsonProperty("created_at") Instant criadoEm,
                               @JsonProperty("updated_at") Instant ultimoUpdate,
                               @JsonProperty("stargazers_count") Integer estrelas,
                               @JsonProperty("language") String linguagem,
                               @JsonProperty("forks_count") Integer forks,
                               @JsonProperty("topics") List<String> topicos,
                               @JsonProperty("open_issues") Integer openIssues) {

}
