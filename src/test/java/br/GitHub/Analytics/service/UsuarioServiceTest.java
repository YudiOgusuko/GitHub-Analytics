package br.GitHub.Analytics.service;

// Os imports dos DTOs, domain e repository: use Alt+Enter no IntelliJ para importar o que ficar vermelho.

import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.paginacao.PaginaDto;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.usuario.UsuarioContribuicoesDto;
import br.GitHub.Analytics.dto.usuario.UsuarioEventosDto;
import br.GitHub.Analytics.dto.usuario.UsuarioNetworkingDto;
import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import br.GitHub.Analytics.dto.usuario.contribuicoes.*;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioUnicoDados;
import br.GitHub.Analytics.dto.usuario.eventos.Eventos;
import br.GitHub.Analytics.dto.usuario.linguagensMaisUsadas.LinguagensMaisUsadas;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final String URL_UNICA = "https://api.github.com/users/";
    private static final String URL_GERAL = "https://api.github.com/search/users?q=";

    private static final String USER = "YudiOgusuko";
    private static final String USER_NORMALIZADO = Utilitarios.tirarEspaco(USER);

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private RepositorioService repositorioService;

    @Mock
    private GitHubClient githubClient;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioUnicoDados criarDados() {
        return UsuarioUnicoDados.builder()
                .userName(USER_NORMALIZADO)
                .nome("Victor Yudi Ogusuko")
                .bio("Computer Science")
                .totalRepositorios(6)
                .seguidores(6)
                .seguindo(10)
                .criadoEm(Instant.parse("2026-02-09T22:40:18Z"))
                .build();
    }

    private RepositoriosMultiplosDto criarRepositorio(String linguagem) {
        return RepositoriosMultiplosDto.builder()
                .nome("Catalogo_de_Filmes")
                .descricao("Catálogo full-stack de filmes e séries: API REST em Java/Spring Boot que consome a OMDb, traduz os dados para português com IA (Gemini) e salva o catálogo no banco. Front-end em HTML, CSS e JS puro. Roda com Docker.")
                .forks(0)
                .linguagem(linguagem)
                .estrelas(1)
                .openIssues(3)
                .topicos(List.of("""
                                css,
                                docker",
                                full-stack,
                                gemini-api,
                                html,
                                java,
                                javascript,
                                mysql,
                                omdb-api,
                                rest-api,
                                spring-boot,
                                spring-data-jpa,
                                testes-integracao,
                                testes-unitarios"""))
                .ultimoUpdate("16 nov. 2026")
                .criadoEm("09 nov. 2026")
                .build();
    }

    private Contribuicoes criarContribuicoes() {
        return Contribuicoes.builder()
                .data(new Data(new User(ContributionsCollection.builder()
                        .contributionCalendar(new ContributionCalendar(398))
                        .build())))
                .build();
    }

    private UsuarioUnicoDto criarUsuarioUnicoDto() {
        return new UsuarioUnicoDto(
                criarDados(),
                List.of(criarRepositorio("Java")),
                new UsuarioContribuicoesDto(criarContribuicoes()));
    }

    private Usuario criarUsuario() {
        return new Usuario(criarUsuarioUnicoDto());
    }

    @Test
    @DisplayName("buscarUsuario: retorna do banco e nao chama a API quando o usuario ja existe")
    void buscarUsuario_quandoExisteNoBanco() {
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO))
                .willReturn(Optional.of(criarUsuario()));

        UsuarioUnicoDto resultado = usuarioService.buscarUsuario(USER);

        assertNotNull(resultado);
        then(githubClient).shouldHaveNoInteractions();
        then(repositorioService).shouldHaveNoInteractions();
        then(usuarioRepository).should().findByUserNameIgnoreCase(USER_NORMALIZADO);
    }

    @Test
    @DisplayName("buscarUsuario: busca na API e salva quando o usuario nao esta no banco")
    void buscarUsuario_quandoNaoExisteNoBanco() {
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.empty());

        given(githubClient.usarRestClient(eq(URL_UNICA), eq(USER_NORMALIZADO), eq(UsuarioUnicoDados.class)))
                .willReturn(criarDados());

        given(repositorioService.buscarTodosRepositoriosNaApi(USER_NORMALIZADO))
                .willReturn(List.of(criarRepositorio("Java")));
        given(githubClient.usarRestClientContribuicoes(any())).willReturn(criarContribuicoes());

        UsuarioUnicoDto resultado = usuarioService.buscarUsuario(USER);

        assertNotNull(resultado);
        assertEquals(USER_NORMALIZADO, resultado.userName());
        then(usuarioRepository).should().save(any(Usuario.class));
    }


    @Test
    @DisplayName("buscarUsuarioUnicoNaApi: monta o DTO, normaliza o nome e salva o usuario")
    void buscarUsuarioUnicoNaApi_sucesso() {
        given(githubClient.usarRestClient(eq(URL_UNICA), eq(USER_NORMALIZADO), eq(UsuarioUnicoDados.class)))
                .willReturn(criarDados());

        given(repositorioService.buscarTodosRepositoriosNaApi(USER_NORMALIZADO))
                .willReturn(List.of(criarRepositorio("Java"), criarRepositorio("Python")));

        given(githubClient.usarRestClientContribuicoes(any())).willReturn(criarContribuicoes());

        UsuarioUnicoDto resultado = usuarioService.buscarUsuarioUnicoNaApi(USER);

        assertNotNull(resultado);
        assertEquals(USER_NORMALIZADO, resultado.userName());
        then(repositorioService).should().buscarTodosRepositoriosNaApi(USER_NORMALIZADO);
        then(usuarioRepository).should().save(any(Usuario.class));
    }

    @Test
    @DisplayName("buscarUsuarioUnicoNaApi: lanca NotFoundException e nao salva quando a API nao retorna o usuario")
    void buscarUsuarioUnicoNaApi_usuarioNaoEncontrado() {
        given(githubClient.usarRestClient(eq(URL_UNICA), eq(USER_NORMALIZADO), eq(UsuarioUnicoDados.class)))
                .willReturn(null);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> usuarioService.buscarUsuarioUnicoNaApi(USER));

        assertTrue(ex.getMessage().contains(USER_NORMALIZADO));
        then(repositorioService).shouldHaveNoInteractions();
        then(usuarioRepository).should(org.mockito.Mockito.never()).save(any());
    }

    @Test
    @DisplayName("buscarUsuariosMultiplosNaApi: monta a URL de busca e mapeia a pagina")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void buscarUsuariosMultiplosNaApi() {
        PaginaDto paginaOrigem = mock(PaginaDto.class);
        PaginaDto paginaMapeada = mock(PaginaDto.class);

        given(githubClient.buscarPaginaMultiplo(eq(URL_GERAL + "yudi"), eq(1), eq(10), any(ParameterizedTypeReference.class)))
                .willReturn(paginaOrigem);
        given(paginaOrigem.mapear(any())).willReturn(paginaMapeada);

        PaginaDto<UsuarioNetworkingDto> resultado = usuarioService.buscarUsuariosMultiplosNaApi("yudi", 1, 10);

        assertSame(paginaMapeada, resultado);
    }

    @Test
    @DisplayName("buscarEventosNaApi: usa o total de eventos retornados como total da pagina")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void buscarEventosNaApi() {
        List eventos = List.of(mock(Eventos.class), mock(Eventos.class), mock(Eventos.class));
        PaginaDto paginaOrigem = mock(PaginaDto.class);
        PaginaDto paginaMapeada = mock(PaginaDto.class);
        PaginaDto paginaComTotal = mock(PaginaDto.class);

        given(githubClient.buscarTodas(eq(URL_UNICA + USER_NORMALIZADO + "/events"), any(ParameterizedTypeReference.class)))
                .willReturn(eventos);

        given(githubClient.buscarPagina(eq(URL_UNICA + USER_NORMALIZADO + "/events"), eq(1), eq(10), any(ParameterizedTypeReference.class)))
                .willReturn(paginaOrigem);

        given(paginaOrigem.mapear(any())).willReturn(paginaMapeada);
        given(paginaMapeada.comTotal(3L)).willReturn(paginaComTotal);

        PaginaDto<UsuarioEventosDto> resultado = usuarioService.buscarEventosNaApi(USER, 1, 10);

        assertSame(paginaComTotal, resultado);
    }

    @Test
    @DisplayName("buscarContribuicoes: retorna do banco e nao chama a API quando o usuario existe")
    void buscarContribuicoes_quandoExisteNoBanco() {
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO))
                .willReturn(Optional.of(criarUsuario()));

        UsuarioContribuicoesDto resultado = usuarioService.buscarContribuicoes(USER);

        assertNotNull(resultado);
        then(githubClient).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("buscarContribuicoes: busca na API quando o usuario nao esta no banco")
    void buscarContribuicoes_quandoNaoExisteNoBanco() {
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.empty());
        given(githubClient.usarRestClientContribuicoes(any())).willReturn(criarContribuicoes());

        UsuarioContribuicoesDto resultado = usuarioService.buscarContribuicoes(USER);

        assertNotNull(resultado);
        then(githubClient).should().usarRestClientContribuicoes(any());
    }

    @Test
    @DisplayName("buscarContribuicoesNaApi: envia a query GraphQL com o login normalizado")
    @SuppressWarnings("unchecked")
    void buscarContribuicoesNaApi_sucesso() {
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        given(githubClient.usarRestClientContribuicoes(captor.capture())).willReturn(criarContribuicoes());

        UsuarioContribuicoesDto resultado = usuarioService.buscarContribuicoesNaApi(USER);

        assertNotNull(resultado);

        Map<String, Object> corpo = captor.getValue();
        assertTrue(((String) corpo.get("query")).contains("contributionsCollection"));
        Map<String, Object> variaveis = (Map<String, Object>) corpo.get("variables");
        assertEquals(USER_NORMALIZADO, variaveis.get("login"));
    }

    @Test
    @DisplayName("buscarContribuicoesNaApi: lanca NotFoundException quando a API retorna null")
    void buscarContribuicoesNaApi_naoEncontrado() {
        given(githubClient.usarRestClientContribuicoes(any())).willReturn(null);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> usuarioService.buscarContribuicoesNaApi(USER));

        assertTrue(ex.getMessage().contains(USER_NORMALIZADO));
    }

    @Test
    @DisplayName("buscarSeguidoresNaApi: usa a quantidade de seguidores do perfil como total")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void buscarSeguidoresNaApi() {
        UsuarioUnicoDados dados = criarDados();
        PaginaDto paginaOrigem = mock(PaginaDto.class);
        PaginaDto paginaMapeada = mock(PaginaDto.class);
        PaginaDto paginaComTotal = mock(PaginaDto.class);

        given(githubClient.usarRestClient(eq(URL_UNICA), eq(USER_NORMALIZADO), eq(UsuarioUnicoDados.class)))
                .willReturn(dados);

        given(githubClient.buscarPagina(eq(URL_UNICA + USER_NORMALIZADO + "/followers"), eq(1), eq(10), any(ParameterizedTypeReference.class)))
                .willReturn(paginaOrigem);

        given(paginaOrigem.mapear(any())).willReturn(paginaMapeada);
        given(paginaMapeada.comTotal(Long.valueOf(dados.seguidores()))).willReturn(paginaComTotal);

        PaginaDto<UsuarioNetworkingDto> resultado = usuarioService.buscarSeguidoresNaApi(USER, 1, 10);

        assertSame(paginaComTotal, resultado);
    }

    @Test
    @DisplayName("buscarSeguindoNaApi: usa a quantidade de seguindo do perfil como total")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void buscarSeguindoNaApi() {
        UsuarioUnicoDados dados = criarDados();
        PaginaDto paginaOrigem = mock(PaginaDto.class);
        PaginaDto paginaMapeada = mock(PaginaDto.class);
        PaginaDto paginaComTotal = mock(PaginaDto.class);

        given(githubClient.usarRestClient(eq(URL_UNICA), eq(USER_NORMALIZADO), eq(UsuarioUnicoDados.class)))
                .willReturn(dados);
        given(githubClient.buscarPagina(eq(URL_UNICA + USER_NORMALIZADO + "/following"), eq(1), eq(10), any(ParameterizedTypeReference.class)))
                .willReturn(paginaOrigem);
        given(paginaOrigem.mapear(any())).willReturn(paginaMapeada);
        given(paginaMapeada.comTotal(Long.valueOf(dados.seguindo()))).willReturn(paginaComTotal);

        PaginaDto<UsuarioNetworkingDto> resultado = usuarioService.buscarSeguindoNaApi(USER, 1, 10);

        assertSame(paginaComTotal, resultado);
    }

    @Test
    @DisplayName("rankingLinguagens: ordena da mais usada para a menos usada e ignora linguagem null")
    void rankingLinguagens_ordenaIgnorandoNulos() {
        given(repositorioService.buscarRepositoriosMultiplos(USER)).willReturn(List.of(
                criarRepositorio("Java"),
                criarRepositorio("Python"),
                criarRepositorio("Java"),
                criarRepositorio(null),
                criarRepositorio("JavaScript"),
                criarRepositorio("Java"),
                criarRepositorio("Python")));

        LinguagensMaisUsadas resultado = usuarioService.rankingLinguagens(USER);

        assertEquals(USER, resultado.userName());
        assertEquals(List.of("Java", "Python", "JavaScript"), resultado.listaLinguagens());
    }

    @Test
    @DisplayName("rankingLinguagens: retorna lista vazia quando nao ha repositorios")
    void rankingLinguagens_semRepositorios() {
        given(repositorioService.buscarRepositoriosMultiplos(USER)).willReturn(List.of());

        LinguagensMaisUsadas resultado = usuarioService.rankingLinguagens(USER);

        assertTrue(resultado.listaLinguagens().isEmpty());
    }
}