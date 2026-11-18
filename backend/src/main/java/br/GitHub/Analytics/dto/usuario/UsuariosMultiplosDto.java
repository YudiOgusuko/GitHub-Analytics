package br.GitHub.Analytics.dto.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UsuariosMultiplosDto<T>(
        @JsonProperty("total_count") Long totalCount,
        @JsonProperty("items") List<T> items) {
}
