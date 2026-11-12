package br.GitHub.Analytics.dto.usuario;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonPropertyOrder({
        "totalDeSeguidores",
        "userName"})

public record UsuarioSeguidoresDto (Integer totalDeSeguidores,
                                    List<UsuarioNetworkingDto> userName) {

    public UsuarioSeguidoresDto(List<UsuarioNetworkingDto> networking) {
        this(contarSeguidores(networking),networking);
    }

    private static Integer contarSeguidores (List<UsuarioNetworkingDto> lista) {
        if (lista == null || lista.isEmpty()) {
            return 0;
        }
        return lista.size();
    }
}
