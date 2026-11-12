package br.GitHub.Analytics.dto.usuario;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonPropertyOrder({
        "totalDeSeguindo",
        "userName"})

public record UsuarioSeguindoDto(Integer totalDeSeguindo,
                                 List<UsuarioNetworkingDto> userName) {

    public UsuarioSeguindoDto(List<UsuarioNetworkingDto> networking) {
        this(contarSeguidores(networking),networking);
    }

    private static Integer contarSeguidores (List<UsuarioNetworkingDto> lista) {
        if (lista == null || lista.isEmpty()) {
            return 0;
        }
        return lista.size();
    }
}
