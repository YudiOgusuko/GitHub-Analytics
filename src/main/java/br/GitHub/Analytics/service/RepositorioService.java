package br.GitHub.Analytics.service;

import br.GitHub.Analytics.dto.repositorio.RepositorioUnicoDto;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.repositorio.branches.Branches;
import br.GitHub.Analytics.dto.repositorio.commits.Commits;
import br.GitHub.Analytics.dto.repositorio.contribuidores.Contribuidores;
import br.GitHub.Analytics.dto.repositorio.dados.RepositorioDados;
import br.GitHub.Analytics.dto.repositorio.pulls.Pulls;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IRepositorioRepository;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

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

        List<RepositorioDados> repositorioDados = usarRestClientLista(URL_USUARIO, usuario, "/repos", new ParameterizedTypeReference<>() {});

        if(repositorioDados == null) {
            throw new NotFoundException(String.format("Nenhum repositório foi encontrado para o usuário '%s'.", usuario));
        }

        return repositorioDados.stream()
                .map(RepositoriosMultiplosDto::new)
                .toList();
    }

    public RepositorioUnicoDto buscarRepositorioUnicoNaApi(String usuario, String repositorio) {
        usuario = Utilitarios.tirarEspaco(usuario).toLowerCase();
        String uri = "/" + Utilitarios.formatarString(repositorio);

        List<RepositorioDados> repositorioDados = usarRestClientLista(URL_REPOSITORIO, usuario, uri, new ParameterizedTypeReference<>() {});

        if(repositorioDados == null || repositorioDados.isEmpty()) {
            throw new NotFoundException(String.format("Repositório '%s' não encontrado para o usuário '%s'.", repositorio, usuario));
        }

        List<Branches> branches = usarRestClientParaDadosDoRepositorio(usuario, uri, "/branches", new ParameterizedTypeReference<>() {});
        Map<String, Integer> linguagens = usarRestClientParaLinguagens(usuario, uri);
        List<Contribuidores> contribuidores = usarRestClientParaDadosDoRepositorio(usuario, uri, "/contributors", new ParameterizedTypeReference<>() {});
        List<Commits> commits = usarRestClientParaDadosDoRepositorio(usuario, uri, "/commits", new ParameterizedTypeReference<>() {});
        List<Pulls> pulls = usarRestClientParaDadosDoRepositorio(usuario, uri, "/pulls", new ParameterizedTypeReference<>() {});

        return new RepositorioUnicoDto(repositorioDados, branches, linguagens, contribuidores, commits, pulls);
    }

    private <T> T usarRestClientLista(String url, String usuario, String uri, ParameterizedTypeReference<T> responseType) {
        return restClient.get()
                .uri(url + usuario + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }

    private <T> T usarRestClientParaDadosDoRepositorio(String usuario, String uri, String complemento, ParameterizedTypeReference<T> responseType) {
        String uriCompleta = usuario + uri + complemento;

        return restClient.get()
                .uri(URL_REPOSITORIO + uriCompleta)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }

    private Map<String, Integer> usarRestClientParaLinguagens(String usuario, String uri) {
        String uriCompleta = usuario + uri + "/languages";

        return restClient.get()
                .uri(URL_REPOSITORIO + uriCompleta)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
