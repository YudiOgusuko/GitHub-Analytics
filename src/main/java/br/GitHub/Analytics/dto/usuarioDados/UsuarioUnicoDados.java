package br.GitHub.Analytics.dto.usuarioDados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioUnicoDados(@JsonProperty("login") String userName,
                                @JsonProperty("name") String nome,
                                @JsonProperty("avatar_url") String perfil,
                                String bio,
                                @JsonProperty("repos_url") String repositoriosUrl,
                                @JsonProperty("public_repos") Integer repositorios,
                                @JsonProperty("followers") Integer seguidores,
                                @JsonProperty("following") Integer seguindo,
                                @JsonProperty("created_at") Instant criadoEm) {
}
