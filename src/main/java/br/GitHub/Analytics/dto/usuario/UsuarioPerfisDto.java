package br.GitHub.Analytics.dto.usuario;

import br.GitHub.Analytics.dto.usuario.dados.PerfilDados;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "userName"})

public record UsuarioPerfisDto(String userName) {

    public UsuarioPerfisDto(PerfilDados perfil) {
        this(perfil.userName());
    }
}
