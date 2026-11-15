package br.GitHub.Analytics.service;

import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.usuario.*;
import br.GitHub.Analytics.dto.usuario.contribuicoes.Contribuicoes;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioMultiplosDados;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioUnicoDados;
import br.GitHub.Analytics.dto.usuario.eventos.Eventos;
import br.GitHub.Analytics.dto.usuario.linguagensMaisUsadas.LinguagensMaisUsadas;
import br.GitHub.Analytics.dto.usuario.networking.Networking;
import br.GitHub.Analytics.handler.exception.InternalServerException;
import br.GitHub.Analytics.handler.exception.NoContentException;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Value("${github.token}")
    private String token;

    private final String URL_UNICA = "https://api.github.com/users/";
    private final String URL_GERAL = "https://api.github.com/search/users?q=";
    private final String URL_GRAPHQL = "https://api.github.com/graphql";
    private final IUsuarioRepository usuarioRepository;
    private final RepositorioService repositorioService;
    private final RestClient restClient;

    public UsuarioUnicoDto buscarUsuario(String nome) {
        String nomeFormatado = Utilitarios.tirarEspaco(nome);
        Optional<Usuario> usuario = usuarioRepository.findByUserNameIgnoreCase(nomeFormatado);

        return usuario.map(UsuarioUnicoDto::new).orElseGet(() -> buscarUsuarioUnicoNaApi(nomeFormatado));
    }

    public UsuarioUnicoDto buscarUsuarioUnicoNaApi(String nome) {
        nome = Utilitarios.tirarEspaco(nome);

        UsuarioUnicoDados usuarioUnicoDados = usarRestClient(URL_UNICA, nome, UsuarioUnicoDados.class);

        if(usuarioUnicoDados == null) {
            throw new NotFoundException(String.format("Nenhum usuário com o username '%s' foi encontrado.", nome));
        }

        List<RepositoriosMultiplosDto> repositorioDtos = repositorioService.buscarRepositoriosMultiplosNaApi(usuarioUnicoDados.userName());
        UsuarioContribuicoesDto contribuicoesDto = buscarContribuicoesNaApi(nome);
        UsuarioUnicoDto usuarioUnicoDto = new UsuarioUnicoDto(usuarioUnicoDados, repositorioDtos, contribuicoesDto);

        Usuario user = new Usuario(usuarioUnicoDto);
        repositorioDtos.forEach(r -> user.adicionarRepositorio(new Repositorio(r)));

        usuarioRepository.save(user);
        return usuarioUnicoDto;
    }

    public UsuariosMultiplosDto buscarUsuariosMultiplosNaApi(String nome) {

        try{
            UsuarioMultiplosDados usuarioMultiplosDados = usarRestClient(URL_GERAL, nome, UsuarioMultiplosDados.class);

            if(usuarioMultiplosDados == null) {
                throw new NotFoundException(String.format("Nenhum usuário com o nome  %s foi encontrado.", nome));
            }

            List<UsuarioPerfisDto> usuarioPerfisDtos = new ArrayList<>();
            usuarioMultiplosDados.perfil().forEach(u -> usuarioPerfisDtos.add(new UsuarioPerfisDto(u)));

            return new UsuariosMultiplosDto(usuarioMultiplosDados.totalDeUsuarios(), usuarioPerfisDtos );
        } catch (HttpClientErrorException e) {
            throw new InternalServerException("Erro inesperado. Por favor, tente novamente.");
        }

    }

    public List<UsuarioEventosDto> buscarEventosNaApi (String nome) {
        nome = Utilitarios.tirarEspaco(nome);

        List<Eventos> eventosDados = usarRestClientLista(nome, "/events", new ParameterizedTypeReference<>(){});

        if (eventosDados == null) {
            throw new NotFoundException(String.format("Nenhum evento foi encontrado para %s.", nome));
        } else if(eventosDados.isEmpty()) {
            throw new NoContentException(String.format("%s não possui nenhum evento recente.", nome));
        }

        return eventosDados.stream()
                .map(UsuarioEventosDto::new)
                .sorted(Comparator.comparing(d -> Utilitarios.ordenarData(d.criadoEm()), Comparator.reverseOrder()))
                .toList();
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

        Contribuicoes contribuicoes = restClient.post()
                .uri(URL_GRAPHQL)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(corpo)
                .retrieve()
                .body(Contribuicoes.class);
        System.out.println(contribuicoes);

        if(contribuicoes == null) {
            throw new NotFoundException(String.format("Nenhum usuário com o nome %s foi encontrado.", nome));
        }

        return new UsuarioContribuicoesDto(contribuicoes);

    }

    public UsuarioSeguidoresDto buscarSeguidoresNaApi(String usuario) {
        usuario = Utilitarios.tirarEspaco(usuario).toLowerCase(Locale.ROOT);
        List<Networking> networkings = usarRestClientLista(usuario, "/followers", new ParameterizedTypeReference<>(){});

        List<UsuarioNetworkingDto> usuarioNetworkingDtos = new ArrayList<>();
        networkings.forEach(n -> usuarioNetworkingDtos.add(new UsuarioNetworkingDto(n)));

        return new UsuarioSeguidoresDto(usuarioNetworkingDtos);
    }

    public UsuarioSeguindoDto buscarSeguindoNaApi(String usuario) {
        usuario = Utilitarios.tirarEspaco(usuario).toLowerCase(Locale.ROOT);
        List<Networking> networkings = usarRestClientLista(usuario, "/following", new ParameterizedTypeReference<>(){});

        List<UsuarioNetworkingDto> usuarioNetworkingDtos = new ArrayList<>();
        networkings.forEach(n -> usuarioNetworkingDtos.add(new UsuarioNetworkingDto(n)));

        return new UsuarioSeguindoDto(usuarioNetworkingDtos);
    }

    private <T> T usarRestClient(String url, String uri, Class<T> responseType) {
        return restClient.get()
                .uri(url + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }

    private <T> T usarRestClientLista(String usuario, String uri, ParameterizedTypeReference<T> responseType) {
        return restClient.get()
                .uri(URL_UNICA + usuario + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }

    public LinguagensMaisUsadas rankingLinguagens(String nome) {
        UsuarioUnicoDto usuarioDto = buscarUsuario(nome);

        List<RepositoriosMultiplosDto> repositorios = repositorioService.buscarRepositoriosMultiplos(usuarioDto.userName());

        List<String> linguagens = repositorios.stream()
                .map(RepositoriosMultiplosDto::linguagem)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        return new LinguagensMaisUsadas(usuarioDto.userName(), linguagens);
    }
}
