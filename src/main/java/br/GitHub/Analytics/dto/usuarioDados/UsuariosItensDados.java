package br.GitHub.Analytics.dto.usuarioDados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuariosItensDados(@JsonProperty("login") String userName,
                                 @JsonProperty("avatar_url") String perfil) {
}
