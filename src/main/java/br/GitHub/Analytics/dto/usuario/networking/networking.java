package br.GitHub.Analytics.dto.usuario.networking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record networking(@JsonProperty("login") String userName,
                         @JsonProperty("avatar_url") String avatar ) {
}
