package br.GitHub.Analytics.dto.usuario.dados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioMultiplosDados(@JsonProperty("total_count") Integer totalDeUsuarios,
                                    List<PerfilDados> perfil) {

}
