package br.GitHub.Analytics.dto;

import br.GitHub.Analytics.dto.usuarioDados.UsuarioMultiplosDados;
import br.GitHub.Analytics.dto.usuarioDados.UsuariosItensDados;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.util.List;

@Builder
@JsonPropertyOrder({
        "totalDeUsuarios",
        "userName",
        "perfil"
})
public record UsuarioMultiplosDto(Integer totalDeUsuarios,
                                  List<UsuariosItensDados> usuarios) {

    public UsuarioMultiplosDto(UsuarioMultiplosDados dados, List<UsuariosItensDados> usuariosDados) {
        this(dados.totalDeUsuarios(), usuariosDados);
    }
}
