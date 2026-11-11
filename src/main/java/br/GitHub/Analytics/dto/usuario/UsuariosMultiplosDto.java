package br.GitHub.Analytics.dto.usuario;

import br.GitHub.Analytics.dto.usuario.dados.PerfilDados;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "totalDeUsuarios",
        "userName",
        "perfil"
})
public record UsuariosMultiplosDto(Integer totalDeUsuarios,
                                   List<PerfilDados> usuarios) {
}
