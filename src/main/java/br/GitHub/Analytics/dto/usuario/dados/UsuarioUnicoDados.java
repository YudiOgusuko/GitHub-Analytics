package br.GitHub.Analytics.dto.usuario.dados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioUnicoDados(@JsonProperty("login") String userName,
                                @JsonProperty("avatar_url") String avatar,
                                @JsonProperty("following_url") String seguidoresUrl,
                                @JsonProperty("followers_url") String seguindoUrl,
                                @JsonProperty("name") String nome,
                                @JsonProperty("bio") String bio,
                                @JsonProperty("public_repos") Integer totalRepositorios,
                                @JsonProperty("followers") Integer seguidores,
                                @JsonProperty("following") Integer seguindo,
                                @JsonProperty("created_at") Instant criadoEm) {
}
