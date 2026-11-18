package br.GitHub.Analytics.dto.paginacao;

import lombok.Builder;

import java.util.List;
import java.util.function.Function;

@Builder
public record PaginaDto<T>(List<T> conteudo,
                           int pagina,
                           int tamanho,
                           boolean temProxima,
                           Long total) {

    public <R> PaginaDto<R> mapear(Function<T, R> funcao) {
        return new PaginaDto<>(conteudo.stream().map(funcao).toList(), pagina, tamanho, temProxima, total);
    }

    public PaginaDto<T> comTotal(Long novoTotal) {
        return new PaginaDto<>(conteudo, pagina, tamanho, temProxima, novoTotal);
    }
}
