package br.GitHub.Analytics.service;

import br.GitHub.Analytics.dto.RepositorioDto;
import br.GitHub.Analytics.dto.repositorioDados.RepositorioDados;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IRepositorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositorioService {

    @Value("${github.token}")
    private String token;

    private final IRepositorioRepository repositorioRepository;
    private final RestClient restClient;

    public List<RepositorioDto> buscarRepositorioNaApi(String url) {

        List<RepositorioDados> repositorioDados = restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if(repositorioDados == null || repositorioDados.isEmpty()) {
            throw new NotFoundException("Nenhum repositório foi encontrado.");
        }

        return repositorioDados.stream()
                .map(RepositorioDto::new)
                .toList();
    }
}
