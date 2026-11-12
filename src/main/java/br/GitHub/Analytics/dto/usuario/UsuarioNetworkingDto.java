package br.GitHub.Analytics.dto.usuario;

import br.GitHub.Analytics.dto.usuario.networking.Networking;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "userName"})

public record UsuarioNetworkingDto(String userName) {

    public UsuarioNetworkingDto(Networking networking) {
        this(networking.userName());
    }
}
