package br.GitHub.Analytics.service;

// Imports de DTOs, domain e repository: use Alt+Enter no IntelliJ para importar o que ficar vermelho.
import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.repositorio.RepositorioUnicoDto;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.repositorio.dados.RepositorioDados;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IRepositorioRepository;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RepositorioServiceTest {

    private static final String URL_USERS = "https://api.github.com/users/";
    private static final String URL_REPOS = "https://api.github.com/repos/";

    private static final String USER = "YudiOgusuko";
    private static final String USER_NORMALIZADO = Utilitarios.tirarEspaco(USER);
    private static final String REPO = "GitHub-Analytics";

    @Mock
    private IRepositorioRepository repositorioRepository;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private GitHubClient githubClient;

    @InjectMocks
    private RepositorioService repositorioService;

    private RepositoriosMultiplosDto criarRepositorioDto(String linguagem) {
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

    private RepositorioDados criarRepositorioDados() {
        return RepositorioDados.builder()
                .id(1L)
                .nome("Spring_Data_Youtube")
                .descricao("Tutorial de Spring Data pro @Youtube")
                .criadoEm(Instant.parse("2026-08-25T01:39:19Z"))
                .ultimoUpdate(Instant.parse("2026-09-03T19:42:08Z"))
                .estrelas(8)
                .linguagem("Java")
                .forks(0)
                .openIssues(2).build();
    }

    private Usuario criarUsuarioMock() {
        Usuario usuario = mock(Usuario.class);
        given(usuario.getId()).willReturn(1L);
        return usuario;
    }

    @Test
    @DisplayName("buscarRepositoriosMultiplos: le do banco e nao chama a API quando o usuario tem repositorios salvos")
    void buscarRepositoriosMultiplos_doBanco() {
        Usuario usuario = criarUsuarioMock();
        List<Repositorio> salvos = List.of(
                new Repositorio(criarRepositorioDto("Java")),
                new Repositorio(criarRepositorioDto("Python")));

        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.of(usuario));
        given(repositorioRepository.findAllByUsuarioId(1L)).willReturn(salvos);

        List<RepositoriosMultiplosDto> resultado = repositorioService.buscarRepositoriosMultiplos(USER);

        assertEquals(2, resultado.size());
        then(githubClient).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("buscarRepositoriosMultiplos: busca na API quando o usuario existe mas nao tem repositorios salvos")
    @SuppressWarnings({"unchecked"})
    void buscarRepositoriosMultiplos_usuarioSemRepositoriosNoBanco() {
        Usuario usuario = criarUsuarioMock();

        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.of(usuario));
        given(repositorioRepository.findAllByUsuarioId(1L)).willReturn(List.of());
        given(githubClient.buscarTodas(eq(URL_USERS + USER_NORMALIZADO + "/repos?sort=created&direction=desc"),
                any(ParameterizedTypeReference.class)))
                .willReturn(List.of(criarRepositorioDados()));

        List<RepositoriosMultiplosDto> resultado = repositorioService.buscarRepositoriosMultiplos(USER);

        assertEquals(1, resultado.size());
        then(githubClient).should().buscarTodas(any(), any());
    }

    @Test
    @DisplayName("buscarRepositoriosMultiplos: busca na API quando o usuario nao esta no banco")
    @SuppressWarnings({"unchecked"})
    void buscarRepositoriosMultiplos_usuarioForaDoBanco() {
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.empty());
        given(githubClient.buscarTodas(eq(URL_USERS + USER_NORMALIZADO + "/repos?sort=created&direction=desc"),
                any(ParameterizedTypeReference.class)))
                .willReturn(List.of(criarRepositorioDados(), criarRepositorioDados()));

        List<RepositoriosMultiplosDto> resultado = repositorioService.buscarRepositoriosMultiplos(USER);

        assertEquals(2, resultado.size());
        then(repositorioRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("buscarTodosRepositoriosNaApi: monta a URL com o usuario normalizado e converte para DTO")
    @SuppressWarnings({"unchecked"})
    void buscarTodosRepositoriosNaApi_sucesso() {
        given(githubClient.buscarTodas(eq(URL_USERS + USER_NORMALIZADO + "/repos?sort=created&direction=desc"),
                any(ParameterizedTypeReference.class)))
                .willReturn(List.of(criarRepositorioDados(), criarRepositorioDados(), criarRepositorioDados()));

        List<RepositoriosMultiplosDto> resultado = repositorioService.buscarTodosRepositoriosNaApi(USER);

        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("buscarTodosRepositoriosNaApi: retorna lista vazia quando o usuario nao tem repositorios")
    @SuppressWarnings({"unchecked"})
    void buscarTodosRepositoriosNaApi_vazio() {
        given(githubClient.buscarTodas(eq(URL_USERS + USER_NORMALIZADO + "/repos?sort=created&direction=desc"),
                any(ParameterizedTypeReference.class)))
                .willReturn(List.of());

        List<RepositoriosMultiplosDto> resultado = repositorioService.buscarTodosRepositoriosNaApi(USER);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("buscarRepositorioUnicoNaApi: consulta repositorio, branches, linguagens, contribuidores, commits e pulls")
    @SuppressWarnings({"unchecked"})
    void buscarRepositorioUnicoNaApi_sucesso() {
        String uri = USER_NORMALIZADO + "/" + REPO;

        given(githubClient.usarRestClient(eq(URL_REPOS), eq(uri), eq(RepositorioDados.class)))
                .willReturn(criarRepositorioDados());
        given(githubClient.usarRestClientUnicoRepositorio(eq(uri + "/branches"), any(ParameterizedTypeReference.class)))
                .willReturn(List.of());
        given(githubClient.usarRestClientUnicoRepositorio(eq(uri + "/languages"), any(ParameterizedTypeReference.class)))
                .willReturn(Map.of("Java", 1000));
        given(githubClient.usarRestClientUnicoRepositorio(eq(uri + "/contributors"), any(ParameterizedTypeReference.class)))
                .willReturn(List.of());
        given(githubClient.usarRestClientUnicoRepositorio(eq(uri + "/commits"), any(ParameterizedTypeReference.class)))
                .willReturn(List.of());
        given(githubClient.usarRestClientUnicoRepositorio(eq(uri + "/pulls"), any(ParameterizedTypeReference.class)))
                .willReturn(List.of());

        RepositorioUnicoDto resultado = repositorioService.buscarRepositorioUnicoNaApi(USER, REPO);

        assertNotNull(resultado);
        then(githubClient).should().usarRestClient(URL_REPOS, uri, RepositorioDados.class);
    }

    @Test
    @DisplayName("buscarRepositorioUnicoNaApi: lanca NotFoundException e nao consulta os detalhes quando o repositorio nao existe")
    @SuppressWarnings({"unchecked"})
    void buscarRepositorioUnicoNaApi_naoEncontrado() {
        String uri = USER_NORMALIZADO + "/" + REPO;

        given(githubClient.usarRestClient(eq(URL_REPOS), eq(uri), eq(RepositorioDados.class)))
                .willReturn(null);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> repositorioService.buscarRepositorioUnicoNaApi(USER, REPO));

        assertTrue(ex.getMessage().contains(REPO));
        assertTrue(ex.getMessage().contains(USER_NORMALIZADO));
        then(githubClient).should(never()).usarRestClientUnicoRepositorio(any(), any(ParameterizedTypeReference.class));
    }
}