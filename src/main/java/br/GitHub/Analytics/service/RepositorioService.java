package br.GitHub.Analytics.service;

import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.repositorio.dados.RepositorioDados;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IRepositorioRepository;
import br.GitHub.Analytics.repository.IUsuarioRepository;
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

    private final String URL_USUARIO = "https://api.github.com/users/";
    private final String URL_REPOSITORIO = "https://api.github.com/repos/";
    private final IRepositorioRepository repositorioRepository;
    private final IUsuarioRepository usuarioRepository;
    private final RestClient restClient;

    public List<RepositoriosMultiplosDto> buscarRepositoriosMultiplos(String usuario) {
        String usuarioFormatado = Utilitarios.tirarEspaco(usuario);

        return usuarioRepository.findByUserNameIgnoreCase(usuarioFormatado)
                .map(user -> repositorioRepository.findByUsuarioId(user.getId()))
                .filter(repositorios -> !repositorios.isEmpty())
                .map(repositorios -> repositorios.stream()
                        .map(RepositoriosMultiplosDto::new)
                        .toList())
                .orElseGet(() -> buscarRepositoriosMultiplosNaApi(usuarioFormatado));
    }

    public List<RepositoriosMultiplosDto> buscarRepositoriosMultiplosNaApi(String usuario) {
        usuario = Utilitarios.tirarEspaco(usuario);

        List<RepositorioDados> repositorioDados = usarRestClientLista(URL_USUARIO, usuario, "repos", new ParameterizedTypeReference<>() {});

        if(repositorioDados == null) {
            throw new NotFoundException(String.format("Nenhum repositório foi encontrado para o usuário '%s'.", usuario));
        }

        return repositorioDados.stream()
                .map(RepositoriosMultiplosDto::new)
                .toList();
    }

    private <T> T usarRestClientLista(String url, String usuario, String uri, ParameterizedTypeReference<T> responseType) {
        return restClient.get()
                .uri(url + usuario + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }
}
