package br.GitHub.Analytics.service;

import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.repositorio.RepositorioDto;
import br.GitHub.Analytics.dto.usuario.UsuarioContribuicoesDto;
import br.GitHub.Analytics.dto.usuario.UsuarioEventosDto;
import br.GitHub.Analytics.dto.usuario.UsuarioMultiplosDto;
import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import br.GitHub.Analytics.dto.usuario.contribuicoes.Contribuicoes;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioMultiplosDados;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioUnicoDados;
import br.GitHub.Analytics.dto.usuario.eventos.EventosDados;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
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

        List<RepositorioDto> repositorioDtos = repositorioService.buscarRepositorioNaApi(usuarioUnicoDados.repositoriosUrl());
        UsuarioContribuicoesDto contribuicoesDto = buscarContribuicoesNaApi(nome);
        UsuarioUnicoDto usuarioUnicoDto = new UsuarioUnicoDto(usuarioUnicoDados, repositorioDtos, contribuicoesDto);

        Usuario user = new Usuario(usuarioUnicoDto);
        repositorioDtos.forEach(r -> user.adicionarRepositorio(new Repositorio(r)));

        usuarioRepository.save(user);
        return usuarioUnicoDto;
    }

    public UsuarioMultiplosDto buscarUsuariosMultiplosNaApi(String nome) {
        nome = Utilitarios.tirarEspaco(nome);

        UsuarioMultiplosDados usuarioMultiplosDados = usarRestClient(URL_GERAL, nome, UsuarioMultiplosDados.class);

        if(usuarioMultiplosDados == null || Utilitarios.verificarCamposNull(usuarioMultiplosDados)) {
            throw new NotFoundException(String.format("Nenhum usuário com o nome  %s foi encontrado.", nome));
        }

        return new UsuarioMultiplosDto(usuarioMultiplosDados.totalDeUsuarios(), usuarioMultiplosDados.perfil());
    }

    public List<UsuarioEventosDto> buscarEventosNaApi (String nome) {
        nome = Utilitarios.tirarEspaco(nome);

        List<EventosDados> eventosDados = usarRestClientLista(URL_UNICA, nome, "/events", new ParameterizedTypeReference<>(){});

        if(eventosDados == null || eventosDados.isEmpty()) {
            throw new NotFoundException(String.format("Nenhum evento foi encontrado para o usuário %s.", nome));
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

    private <T> T usarRestClient(String url, String uri, Class<T> responseType) {
        return restClient.get()
                .uri(url + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }

    private <T> T usarRestClientLista(String url, String nome, String uri, ParameterizedTypeReference<T> responseType) {
        return restClient.get()
                .uri(url + nome + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(responseType);
    }
}
