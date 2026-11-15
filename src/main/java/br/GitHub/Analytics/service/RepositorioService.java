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

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RepositorioService {

    private final IRepositorioRepository repositorioRepository;
    private final IUsuarioRepository usuarioRepository;
    private final Utilitarios utilitarios;

    public List<RepositoriosMultiplosDto> buscarRepositoriosMultiplos(String usuario) {
        String usuarioFormatado = Utilitarios.tirarEspaco(usuario);

        return usuarioRepository.findByUserNameIgnoreCase(usuarioFormatado)
                .map(user -> repositorioRepository.findAllByUsuarioId(user.getId()))
                .filter(repositorios -> !repositorios.isEmpty())
                .map(repositorios -> repositorios.stream()
                        .map(RepositoriosMultiplosDto::new)
                        .sorted(Comparator.comparing(d -> Utilitarios.formatarStringParaData(d.criadoEm()), Comparator.reverseOrder()))
                        .toList())
                .orElseGet(() ->(buscarRepositoriosMultiplosNaApi(usuarioFormatado)));
    }

    public List<RepositoriosMultiplosDto> buscarRepositoriosMultiplosNaApi(String usuario) {
        usuario = Utilitarios.tirarEspaco(usuario);

        List<RepositorioDados> repositorioDados = utilitarios.usarRestClientLista("https://api.github.com/users/", usuario, "/repos", new ParameterizedTypeReference<>() {});

        if(repositorioDados == null || repositorioDados.isEmpty()) throw new NotFoundException(String.format("Nenhum repositório foi encontrado para o usuário '%s'.", usuario));

        return repositorioDados.stream()
                .map(RepositoriosMultiplosDto::new)
                .sorted(Comparator.comparing(d -> Utilitarios.formatarStringParaData(d.criadoEm()), Comparator.reverseOrder()))
                .toList();
    }

    public RepositorioUnicoDto buscarRepositorioUnicoNaApi(String usuario, String repositorio) {
        usuario = Utilitarios.tirarEspaco(usuario);

        String uriRepositorio = String.format("%s/%s", usuario, repositorio);
        RepositorioDados repositorioDados = utilitarios.usarRestClient("https://api.github.com/repos/", uriRepositorio, RepositorioDados.class);

        if(repositorioDados == null) throw new NotFoundException(String.format("Repositório '%s' não encontrado para o usuário '%s'.", repositorio, usuario));

        List<Branches> branches = utilitarios.usarRestClientUnicoRepositorio(uriRepositorio + "/branches", new ParameterizedTypeReference<>() {});
        Map<String, Integer> linguagens = utilitarios.usarRestClientUnicoRepositorio(uriRepositorio + "/languages", new ParameterizedTypeReference<>() {});
        List<Contribuidores> contribuidores = utilitarios.usarRestClientUnicoRepositorio(uriRepositorio + "/contributors", new ParameterizedTypeReference<>() {});
        List<Commits> commits = utilitarios.usarRestClientUnicoRepositorio(uriRepositorio + "/commits", new ParameterizedTypeReference<>() {});
        List<Pulls> pulls = utilitarios.usarRestClientUnicoRepositorio(uriRepositorio + "/pulls", new ParameterizedTypeReference<>() {});

        return new RepositorioUnicoDto(repositorioDados, branches, linguagens, contribuidores, commits, pulls);
    }
}
