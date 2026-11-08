package br.GitHub.Analytics.dto.usuarioDados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioMultiplosDados(@JsonProperty("total_count") Integer totalDeUsuarios,
                                    List<UsuariosItensDados> items) {

}
