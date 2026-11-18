package br.GitHub.Analytics.service;

import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.paginacao.PaginaDto;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.usuario.*;
import br.GitHub.Analytics.dto.usuario.contribuicoes.Contribuicoes;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioUnicoDados;
import br.GitHub.Analytics.dto.usuario.eventos.Eventos;
import br.GitHub.Analytics.dto.usuario.linguagensMaisUsadas.LinguagensMaisUsadas;
import br.GitHub.Analytics.dto.usuario.networking.Networking;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final String URL_UNICA = "https://api.github.com/users/";
    private final String URL_GERAL = "https://api.github.com/search/users?q=";
    private final IUsuarioRepository usuarioRepository;
    private final RepositorioService repositorioService;
    private final GitHubClient githubClient;

    public UsuarioUnicoDto buscarUsuario(String nome) {
        String nomeFormatado = Utilitarios.tirarEspaco(nome);
        Optional<Usuario> usuario = usuarioRepository.findByUserNameIgnoreCase(nomeFormatado);

        return usuario.map(UsuarioUnicoDto::new).orElseGet(() -> buscarUsuarioUnicoNaApi(nomeFormatado));
    }

    public UsuarioUnicoDto buscarUsuarioUnicoNaApi(String nome) {
        nome = Utilitarios.tirarEspaco(nome);

        UsuarioUnicoDados usuarioUnicoDados = githubClient.usarRestClient(URL_UNICA, nome, UsuarioUnicoDados.class);

        if(usuarioUnicoDados == null) throw new NotFoundException(String.format("Nenhum usuário com o username '%s' foi encontrado.", nome));

        List<RepositoriosMultiplosDto> repositorioDtos = repositorioService.buscarTodosRepositoriosNaApi(usuarioUnicoDados.userName());
        UsuarioContribuicoesDto contribuicoesDto = buscarContribuicoesNaApi(nome);
        UsuarioUnicoDto usuarioUnicoDto = new UsuarioUnicoDto(usuarioUnicoDados, repositorioDtos, contribuicoesDto);

        Usuario user = new Usuario(usuarioUnicoDto);
        repositorioDtos.forEach(r -> user.adicionarRepositorio(new Repositorio(r)));

        usuarioRepository.save(user);

        return usuarioUnicoDto;
    }

    public PaginaDto<UsuarioNetworkingDto> buscarUsuariosMultiplosNaApi(String nome, Integer pagina, Integer tamanho) {
        return githubClient.buscarPaginaMultiplo(URL_GERAL + nome, pagina, tamanho, new ParameterizedTypeReference<UsuariosMultiplosDto<Networking>>() {})
                .mapear(UsuarioNetworkingDto::new);
    }

    public PaginaDto<UsuarioEventosDto> buscarEventosNaApi (String nome, Integer pagina, Integer tamanho) {
        nome = Utilitarios.tirarEspaco(nome);
        List<Eventos> eventosDados = githubClient.buscarTodas(URL_UNICA + nome + "/events", new ParameterizedTypeReference<>() {});

        return githubClient.buscarPagina(URL_UNICA + nome + "/events", pagina, tamanho, new ParameterizedTypeReference<List<Eventos>>(){})
                .mapear(UsuarioEventosDto::new)
                .comTotal(Long.valueOf(eventosDados.size()));
    }

    public UsuarioContribuicoesDto buscarContribuicoes(String nome) {
        nome = Utilitarios.tirarEspaco(nome);

        Optional<Usuario> usuario = usuarioRepository.findByUserNameIgnoreCase(nome);
        if(usuario.isPresent()) {
            return new UsuarioContribuicoesDto(usuario.get());
        }

        return buscarContribuicoesNaApi(nome);
    }

    public UsuarioContribuicoesDto buscarContribuicoesNaApi (String nome) {
        nome = Utilitarios.tirarEspaco(nome);

        String query = """
        query($login: String!) {
          user(login: $login) {
            contributionsCollection {
              contributionCalendar { totalContributions }
              totalCommitContributions
              totalIssueContributions
              totalPullRequestContributions
              totalPullRequestReviewContributions
            }
          }
        }
        """;

        Map<String, Object> corpo = Map.of(
                "query", query,
                "variables", Map.of("login", nome));

        Contribuicoes contribuicoes = githubClient.usarRestClientContribuicoes(corpo);
        if(contribuicoes == null) throw new NotFoundException(String.format("Nenhum usuário com o nome %s foi encontrado.", nome));

        return new UsuarioContribuicoesDto(contribuicoes);
    }

    public PaginaDto<UsuarioNetworkingDto> buscarSeguidoresNaApi(String usuario, Integer pagina, Integer tamanho) {
        usuario = Utilitarios.tirarEspaco(usuario);
        UsuarioUnicoDados perfil = githubClient.usarRestClient(URL_UNICA, usuario, UsuarioUnicoDados.class);

        return githubClient.buscarPagina(URL_UNICA + usuario + "/followers", pagina, tamanho, new ParameterizedTypeReference<List<Networking>>() {})
                .mapear(UsuarioNetworkingDto::new)
                .comTotal(Long.valueOf(perfil.seguidores()));
    }

    public PaginaDto<UsuarioNetworkingDto> buscarSeguindoNaApi(String usuario, Integer pagina, Integer tamanho) {
        usuario = Utilitarios.tirarEspaco(usuario);
        UsuarioUnicoDados perfil = githubClient.usarRestClient(URL_UNICA, usuario, UsuarioUnicoDados.class);

        return githubClient.buscarPagina(URL_UNICA + usuario + "/following", pagina, tamanho, new ParameterizedTypeReference<List<Networking>>() {})
                .mapear(UsuarioNetworkingDto::new)
                .comTotal(Long.valueOf(perfil.seguindo()));
    }

    public LinguagensMaisUsadas rankingLinguagens(String nome) {
        List<RepositoriosMultiplosDto> repositorios = repositorioService.buscarRepositoriosMultiplos(nome);

        List<String> linguagens = repositorios.stream()
                .map(RepositoriosMultiplosDto::linguagem)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        return new LinguagensMaisUsadas(nome, linguagens);
    }
}
