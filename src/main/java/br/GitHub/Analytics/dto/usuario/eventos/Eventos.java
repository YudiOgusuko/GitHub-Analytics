package br.GitHub.Analytics.dto.usuario.eventos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Eventos(String id,
                      @JsonProperty("type") String tipo,
                      @JsonProperty("repo") Repo repositorio,
                      @JsonProperty("created_at") Instant criadoEm){

}
