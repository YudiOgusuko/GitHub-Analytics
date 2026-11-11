package br.GitHub.Analytics.dto.usuario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioSeguindoDto(String userName,
                                 String avatar ) {
}
