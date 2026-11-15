package br.GitHub.Analytics.dto.repositorio;

import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.dto.repositorio.dados.RepositorioDados;
import br.GitHub.Analytics.service.Utilitarios;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "nome",
        "descricao",
        "forks",
        "linguagem",
        "estrelas",
        "openIssues",
        "topicos",
        "ultimoUpdate",
        "criadoEm"})

public record RepositoriosMultiplosDto(@JsonIgnore Long id,
                                       String nome,
                                       String descricao,
                                       String criadoEm,
                                       String ultimoUpdate,
                                       Integer estrelas,
                                       String linguagem,
                                       Integer forks,
                                       List<String> topicos,
                                       Integer openIssues){

    public RepositoriosMultiplosDto(RepositorioDados dados) {
        this(dados.id() != null ? dados.id() : 0, dados.nome() != null ? dados.nome() : "N/A", dados.descricao() != null ? dados.descricao() : "N/A",
                Utilitarios.formatarDataParaString(dados.criadoEm()), Utilitarios.formatarDataParaString(dados.ultimoUpdate()), dados.estrelas() != null ? dados.estrelas() : 0,
                dados.linguagem() != null ? dados.linguagem() : "N/A", dados.forks() != null ? dados.forks() : 0,
                dados.topicos() != null ? dados.topicos() : List.of(), dados.openIssues() != null ? dados.openIssues() : 0);
    }

    public RepositoriosMultiplosDto(Repositorio repositorio) {
        this(repositorio.getRepositorioId(), repositorio.getNome(), repositorio.getDescricao(), repositorio.getCriadoEm(),
                repositorio.getUltimoUpdate(), repositorio.getEstrelas(), repositorio.getLinguagem(),
                repositorio.getForks(), repositorio.getTopicos(), repositorio.getOpenIssues());
    }
}
