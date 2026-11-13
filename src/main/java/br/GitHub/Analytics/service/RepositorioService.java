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

import java.util.Comparator;
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
                .map(user -> repositorioRepository.findAllByUsuarioId(user.getId()))
                .filter(repositorios -> !repositorios.isEmpty())
                .map(repositorios -> repositorios.stream()
                        .map(RepositoriosMultiplosDto::new)
                        .sorted(Comparator.comparing(d -> Utilitarios.ordenarData(d.criadoEm()), Comparator.reverseOrder()))
                        .toList())
                .orElseGet(() ->(buscarRepositoriosMultiplosNaApi(usuarioFormatado)));
    }

    public List<RepositoriosMultiplosDto> buscarRepositoriosMultiplosNaApi(String usuario) {
        usuario = Utilitarios.tirarEspaco(usuario);

        List<RepositorioDados> repositorioDados = usarRestClientLista(usuario, new ParameterizedTypeReference<>() {});

        if(repositorioDados == null || repositorioDados.isEmpty()) {
            throw new NotFoundException(String.format("Nenhum repositório foi encontrado para o usuário '%s'.", usuario));
        }

        return repositorioDados.stream()
                .map(RepositoriosMultiplosDto::new)
                .sorted(Comparator.comparing(d -> Utilitarios.ordenarData(d.criadoEm()), Comparator.reverseOrder()))
                .toList();
    }

    public RepositorioUnicoDto buscarRepositorioUnicoNaApi(String usuario, String repositorio) {
        usuario = Utilitarios.tirarEspaco(usuario);

        if(usuarioRepository.existsByUserNameIgnoreCase(usuario)) {
            repositorio = Utilitarios.formatarRepositorioNoBanco(repositorio);
        } else {
            repositorio = Utilitarios.formatarRepositorioNaApi(repositorio);
        }

        String uriRepositorio = String.format("%s/%s", usuario, repositorio);
        RepositorioDados repositorioDados = usarRestClient(uriRepositorio);

        if(repositorioDados == null) {
            throw new NotFoundException(String.format("Repositório '%s' não encontrado para o usuário '%s'.", repositorio, usuario));
        }

        List<Branches> branches = usarRestClientUnicoRepositorio(uriRepositorio + "/branches", new ParameterizedTypeReference<>() {});
        Map<String, Integer> linguagens = usarRestClientUnicoRepositorio(uriRepositorio + "/languages", new ParameterizedTypeReference<>() {});
        List<Contribuidores> contribuidores = usarRestClientUnicoRepositorio(uriRepositorio + "/contributors", new ParameterizedTypeReference<>() {});
        List<Commits> commits = usarRestClientUnicoRepositorio(uriRepositorio + "/commits", new ParameterizedTypeReference<>() {});
        List<Pulls> pulls = usarRestClientUnicoRepositorio(uriRepositorio + "/pulls", new ParameterizedTypeReference<>() {});

        return new RepositorioUnicoDto(repositorioDados, branches, linguagens, contribuidores, commits, pulls);
    }

    private RepositorioDados usarRestClient(String uri) {
        return restClient.get()
                .uri(URL_REPOSITORIO + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(RepositorioDados.class);
    }

    private <T> T usarRestClientLista(String usuario, ParameterizedTypeReference<T> responseType) {
        return restClient.get()
                .uri(URL_USUARIO + usuario + "/repos")
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }

    private <T> T usarRestClientUnicoRepositorio(String uri, ParameterizedTypeReference<T> responseType) {
        return restClient.get()
                .uri(URL_REPOSITORIO + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }
}
