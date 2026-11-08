package br.GitHub.Analytics.dto;

import br.GitHub.Analytics.dto.repositorioDados.RepositorioDados;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonPropertyOrder({
        "nome",
        "pagina",
        "descricao",
        "criadaEm",
        "ultimoUpdate",
        "estrelas",
        "linguagem",
        "forks",
        "openIssues"})
public record RepositorioDto(String nome,
                             String pagina,
                             String descricao,
                             Instant criadaEm,
                             Instant ultimoUpdate,
                             Integer estrelas,
                             String linguagem,
                             Integer forks,
                             Integer openIssues) {

    public RepositorioDto(RepositorioDados dados) {
        this(dados.nome() != null ? dados.nome() : "N/A", dados.pagina() != null ? dados.pagina() : "N/A",
                dados.descricao() != null ? dados.descricao() : "N/A", dados.criadoEm(), dados.ultimoUpdate(),
                dados.estrelas() != null ? dados.estrelas() : 0, dados.linguagem() != null ? dados.linguagem() : "N/A",
                dados.forks() != null ? dados.forks() : 0, dados.openIssues() != null ? dados.openIssues() : 0);
    }
}
