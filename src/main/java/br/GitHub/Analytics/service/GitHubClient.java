package br.GitHub.Analytics.service;

import br.GitHub.Analytics.dto.paginacao.PaginaDto;
import br.GitHub.Analytics.dto.usuario.UsuariosMultiplosDto;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubClient {

    @Value("${github.token}")
    private String token;

    private final RestClient restClient;
    private static final int MAX_PAGINAS = 30;


    public <T> PaginaDto<T> buscarPagina(String url, int pagina, int tamanho,
                                         ParameterizedTypeReference<List<T>> tipo) {

        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("page", pagina)
                .queryParam("per_page", tamanho)
                .build().toUri();

        try {
            ResponseEntity<List<T>> resposta = restClient.get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/vnd.github+json")
                    .retrieve()
                    .toEntity(tipo);

            String link = resposta.getHeaders().getFirst(HttpHeaders.LINK);
            boolean temProxima = link != null && link.contains("rel=\"next\"");
            List<T> conteudo = resposta.getBody() == null ? List.of() : resposta.getBody();

            return new PaginaDto<>(conteudo, pagina, tamanho, temProxima, null);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Nada foi encontrado no GitHub para: " + url);
        }
    }

    public <T> PaginaDto<T> buscarPaginaMultiplo(String url, int pagina, int tamanho, ParameterizedTypeReference<UsuariosMultiplosDto<T>> tipo) {

        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("page", pagina)
                .queryParam("per_page", tamanho)
                .build().toUri();

        try {
            ResponseEntity<UsuariosMultiplosDto<T>> resposta = restClient.get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/vnd.github+json")
                    .retrieve()
                    .toEntity(tipo);

            UsuariosMultiplosDto<T> corpo = resposta.getBody();
            List<T> itens = (corpo == null || corpo.items() == null) ? List.of() : corpo.items();
            long total = (corpo == null || corpo.totalCount() == null) ? 0L : corpo.totalCount();

            String link = resposta.getHeaders().getFirst(HttpHeaders.LINK);
            boolean temProxima = link != null && link.contains("rel=\"next\"");

            return new PaginaDto<>(itens, pagina, tamanho, temProxima, total);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Nada foi encontrado no GitHub para: " + url);
        }
    }

    public <T> List<T> buscarTodas(String url, ParameterizedTypeReference<List<T>> tipo) {
        List<T> todos = new ArrayList<>();
        for (int pagina = 1; pagina <= MAX_PAGINAS; pagina++) {
            PaginaDto<T> p = buscarPagina(url, pagina, 100, tipo);
            todos.addAll(p.conteudo());
            if (!p.temProxima()) break;
        }
        return todos;
    }

    public <T> T usarRestClient(String url, String uri, Class<T> responseType) {
        return restClient.get()
                .uri(url + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }

    public  <T> T usarRestClientUnicoRepositorio(String uri, ParameterizedTypeReference<T> responseType) {
        return restClient.get()
                .uri( "https://api.github.com/repos/" + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }
}
