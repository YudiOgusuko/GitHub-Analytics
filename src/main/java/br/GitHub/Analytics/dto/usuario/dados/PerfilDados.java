package br.GitHub.Analytics.dto.usuario.dados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PerfilDados(@JsonProperty("login") String userName,
                          @JsonProperty("avatar_url") String avatar) {
}
