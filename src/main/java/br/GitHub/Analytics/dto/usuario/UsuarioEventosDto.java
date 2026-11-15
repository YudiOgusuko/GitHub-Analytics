package br.GitHub.Analytics.dto.usuario;

import br.GitHub.Analytics.dto.usuario.eventos.Eventos;
import br.GitHub.Analytics.service.Utilitarios;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id",
        "tipo",
        "repositorioNome",
        "criadoEm"})
public record UsuarioEventosDto(String id,
                                String tipo,
                                String repositorioNome,
                                String criadoEm) {

    public UsuarioEventosDto(Eventos dados) {
        this(dados.id() != null ? dados.id() : "N/A", dados.tipo() != null ? dados.tipo() : "N/A",
                dados.repositorio().nome() != null ? Utilitarios.formatarString(dados.repositorio().nome()) : "N/A",
                dados.criadoEm() != null ? Utilitarios.formatarDataParaString(dados.criadoEm()) : "N/A");
    }
}
