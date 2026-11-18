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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RepositorioService {

    private final IRepositorioRepository repositorioRepository;
    private final IUsuarioRepository usuarioRepository;
    private final GitHubClient githubClient;

    public List<RepositoriosMultiplosDto> buscarRepositoriosMultiplos(String usuario) {
        String usuarioFormatado = Utilitarios.tirarEspaco(usuario);

        return usuarioRepository.findByUserNameIgnoreCase(usuarioFormatado)
                .map(user -> repositorioRepository.findAllByUsuarioId(user.getId()))
                .filter(repositorios -> !repositorios.isEmpty())
                .map(repositorios -> repositorios.stream().map(RepositoriosMultiplosDto::new).toList())
                .orElseGet(() -> buscarTodosRepositoriosNaApi(usuarioFormatado));
    }

    public List<RepositoriosMultiplosDto> buscarTodosRepositoriosNaApi(String usuario) {
        String usuarioFormatado = Utilitarios.tirarEspaco(usuario);

        return githubClient.buscarTodas(
                "https://api.github.com/users/" + usuarioFormatado + "/repos?sort=created&direction=desc",
                        new ParameterizedTypeReference<List<RepositorioDados>>() {})
                .stream()
                .map(RepositoriosMultiplosDto::new)
                .toList();
    }

    public RepositorioUnicoDto buscarRepositorioUnicoNaApi(String usuario, String repositorio) {
        usuario = Utilitarios.tirarEspaco(usuario);

        String uriRepositorio = String.format("%s/%s", usuario, repositorio);
        RepositorioDados repositorioDados = githubClient.usarRestClient("https://api.github.com/repos/", uriRepositorio, RepositorioDados.class);

        if(repositorioDados == null) throw new NotFoundException(String.format("Repositório '%s' não encontrado para o usuário '%s'.", repositorio, usuario));

        List<Branches> branches = githubClient.usarRestClientUnicoRepositorio(uriRepositorio + "/branches", new ParameterizedTypeReference<>() {});
        Map<String, Integer> linguagens = githubClient.usarRestClientUnicoRepositorio(uriRepositorio + "/languages", new ParameterizedTypeReference<>() {});
        List<Contribuidores> contribuidores = githubClient.usarRestClientUnicoRepositorio(uriRepositorio + "/contributors", new ParameterizedTypeReference<>() {});
        List<Commits> commits = githubClient.usarRestClientUnicoRepositorio(uriRepositorio + "/commits", new ParameterizedTypeReference<>() {});
        List<Pulls> pulls = githubClient.usarRestClientUnicoRepositorio(uriRepositorio + "/pulls", new ParameterizedTypeReference<>() {});

        return new RepositorioUnicoDto(repositorioDados, branches, linguagens, contribuidores, commits, pulls);
    }
}
