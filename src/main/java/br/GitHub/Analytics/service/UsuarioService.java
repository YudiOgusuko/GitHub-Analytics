package br.GitHub.Analytics.service;

import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.usuario.*;
import br.GitHub.Analytics.dto.usuario.contribuicoes.Contribuicoes;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioMultiplosDados;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioUnicoDados;
import br.GitHub.Analytics.dto.usuario.eventos.Eventos;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Optional<Usuario> usuario = usuarioRepository.findByuserNameIgnoreCase(nomeFormatado);

        return usuario.map(UsuarioUnicoDto::new).orElseGet(() -> buscarUsuarioUnicoNaApi(nomeFormatado));
    }

    public UsuarioUnicoDto buscarUsuarioUnicoNaApi(String nome) {
        nome = Utilitarios.tirarEspaco(nome);

        UsuarioUnicoDados usuarioUnicoDados = usarRestClient(URL_UNICA, nome, UsuarioUnicoDados.class);

        if(usuarioUnicoDados == null || Utilitarios.verificarCamposNull(usuarioUnicoDados)) {
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

            if(usuarioMultiplosDados == null || Utilitarios.verificarCamposNull(usuarioMultiplosDados)) {
                throw new NotFoundException(String.format("Nenhum usuário com o nome  %s foi encontrado.", nome));
            }

            return new UsuariosMultiplosDto(usuarioMultiplosDados.totalDeUsuarios(), usuarioMultiplosDados.perfil());
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
                .toList();
    }

    public UsuarioContribuicoesDto buscarContribuicoes(String nome) {
        nome = Utilitarios.tirarEspaco(nome);

        Optional<Usuario> usuario = usuarioRepository.findByuserNameIgnoreCase(nome);
        if(usuario.isPresent()) {
            return new UsuarioContribuicoesDto(usuario.get());
        }

        return buscarContribuicoesNaApi(nome);
    }

    public UsuarioContribuicoesDto buscarContribuicoesNaApi (String nome) {
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

    public List<UsuarioSeguidoresDto> buscarSeguidores(String nome) {
        String nomeFormatado = Utilitarios.tirarEspaco(nome);
        Optional<Usuario> usuario = usuarioRepository.findByuserNameIgnoreCase(nomeFormatado);

        if(usuario.isPresent()) {
            return usarRestClientLista(usuario.get().getNome(), "/followers", new ParameterizedTypeReference<>(){});
        }

        UsuarioUnicoDto usuarioUnicoDto = buscarUsuarioUnicoNaApi(nome);
        return buscarSeguidoresNaApi(usuarioUnicoDto.seguidoresUrl());

    }

    public List<UsuarioSeguidoresDto> buscarSeguidoresNaApi(String seguidoresUrl) {
        return usarRestClientNetworking(seguidoresUrl, new ParameterizedTypeReference<>(){});
    }

    public List<UsuarioSeguindoDto> buscarSeguindo(String nome) {
        String nomeFormatado = Utilitarios.tirarEspaco(nome);
        Optional<Usuario> usuario = usuarioRepository.findByuserNameIgnoreCase(nomeFormatado);

        if(usuario.isPresent()) {
            return usarRestClientLista(usuario.get().getUserName(), "/following", new ParameterizedTypeReference<>(){});
        }

        UsuarioUnicoDto usuarioUnicoDto = buscarUsuarioUnicoNaApi(nome);
        return buscarSeguindoNaApi(usuarioUnicoDto.seguindoUrl());
    }

    public List<UsuarioSeguindoDto> buscarSeguindoNaApi(String seguindoUrl) {
        return usarRestClientNetworking(seguindoUrl, new ParameterizedTypeReference<>(){});
    }

    public List<RepositoriosMultiplosDto> buscarRepositoriosUsuario(String nome) {
        nome = Utilitarios.tirarEspaco(nome);
        Optional<Usuario> usuario = usuarioRepository.findByuserNameIgnoreCase(nome);

        if(usuario.isPresent()) {
            return repositorioService.buscarRepositoriosMultiplos(usuario.get().getUserName());
        }

        return repositorioService.buscarRepositoriosMultiplosNaApi(nome);

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

    private <T> T usarRestClientNetworking(String networkingUrl, ParameterizedTypeReference<T> responseType) {
        return restClient.get()
                .uri(networkingUrl)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }
}
