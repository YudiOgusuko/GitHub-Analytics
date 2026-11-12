package br.GitHub.Analytics.dto.usuario;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonPropertyOrder({
        "totalDeUsuarios",
        "userName"})

public record UsuariosMultiplosDto(Integer totalDeUsuarios,
                                   List<UsuarioPerfisDto> userName) {
}
