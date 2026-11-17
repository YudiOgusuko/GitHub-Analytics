package br.GitHub.Analytics.service;

// Imports de DTOs, domain e repository: use Alt+Enter no IntelliJ para importar o que ficar vermelho.
import br.GitHub.Analytics.domain.Snapshot;
import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.snapshot.SnapshotDto;
import br.GitHub.Analytics.dto.usuario.UsuarioContribuicoesDto;
import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import br.GitHub.Analytics.dto.usuario.contribuicoes.*;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioUnicoDados;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IRepositorioRepository;
import br.GitHub.Analytics.repository.ISnapshotRepository;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SnapshotServiceTest {

    private static final String URL_UNICA = "https://api.github.com/users/";

    private static final String USER = "YudiOgusuko";
    private static final String USER_NORMALIZADO = Utilitarios.tirarEspaco(USER);

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private IRepositorioRepository repositorioRepository;

    @Mock
    private ISnapshotRepository snapshotRepository;

    @Mock
    private RepositorioService repositorioService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private SincronizacaoPersistenciaService sincronizacaoPersistenciaService;

    @InjectMocks
    private SnapshotService snapshotService;

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

    private RepositoriosMultiplosDto criarRepositorioDto() {
        return RepositoriosMultiplosDto.builder()
                .nome("Catalogo_de_Filmes")
                .descricao("Catálogo full-stack de filmes e séries: API REST em Java/Spring Boot que consome a OMDb, traduz os dados para português com IA (Gemini) e salva o catálogo no banco. Front-end em HTML, CSS e JS puro. Roda com Docker.")
                .forks(0)
                .linguagem("Java")
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

    private UsuarioContribuicoesDto criarContribuicoesDto() {
        return new UsuarioContribuicoesDto(criarContribuicoes());
    }

    private Usuario criarUsuario() {
        UsuarioUnicoDto dto = new UsuarioUnicoDto(
                criarDados(),
                List.of(criarRepositorioDto()),
                criarContribuicoesDto());
        return new Usuario(dto);
    }

    @Test
    @DisplayName("sincronizar: busca dados atuais na API, gera snapshot e persiste")
    void sincronizar_sucesso() {
        Usuario usuario = criarUsuario();

        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.of(usuario));
        given(gitHubClient.usarRestClient(eq(URL_UNICA), eq(usuario.getUserName()), eq(UsuarioUnicoDados.class)))
                .willReturn(criarDados());
        given(repositorioService.buscarTodosRepositoriosNaApi(usuario.getUserName()))
                .willReturn(List.of(criarRepositorioDto()));
        given(usuarioService.buscarContribuicoesNaApi(USER_NORMALIZADO)).willReturn(criarContribuicoesDto());
        given(repositorioRepository.findAllByUsuarioId(usuario.getId())).willReturn(List.of());

        UsuarioUnicoDto resultado = snapshotService.sincronizarDadosUsuarioERepositorios(USER);

        assertNotNull(resultado);
        assertEquals(USER_NORMALIZADO, resultado.userName());
        then(sincronizacaoPersistenciaService).should().salvar(eq(usuario), any());
    }

    @Test
    @DisplayName("sincronizar: lanca NotFoundException quando o usuario nao esta no banco")
    void sincronizar_usuarioForaDoBanco() {
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> snapshotService.sincronizarDadosUsuarioERepositorios(USER));

        assertTrue(ex.getMessage().contains(USER_NORMALIZADO));
        then(gitHubClient).shouldHaveNoInteractions();
        then(sincronizacaoPersistenciaService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("sincronizar: lanca NotFoundException e nao persiste quando a API nao retorna o usuario")
    void sincronizar_apiNaoRetornaUsuario() {
        Usuario usuario = criarUsuario();

        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.of(usuario));
        given(gitHubClient.usarRestClient(eq(URL_UNICA), eq(usuario.getUserName()), eq(UsuarioUnicoDados.class)))
                .willReturn(null);

        assertThrows(NotFoundException.class,
                () -> snapshotService.sincronizarDadosUsuarioERepositorios(USER));

        then(repositorioService).shouldHaveNoInteractions();
        then(sincronizacaoPersistenciaService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("verHistorico: usa pagina 1 e tamanho 30 quando os parametros sao null")
    void verHistorico_valoresPadrao() {
        Usuario usuario = criarUsuario();
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.of(usuario));
        given(snapshotRepository.findAllByUserName(usuario.getUserName(), PageRequest.of(0, 30)))
                .willReturn(List.of(new Snapshot(usuario)));

        List<SnapshotDto> resultado = snapshotService.verHistorico(USER_NORMALIZADO, null, null);

        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).informacoesAntigas().size());
        then(snapshotRepository).should().findAllByUserName(usuario.getUserName(), PageRequest.of(0, 30));
    }

    @Test
    @DisplayName("verHistorico: converte pagina (base 1) para PageRequest (base 0) com o tamanho informado")
    void verHistorico_paginacaoInformada() {
        Usuario usuario = criarUsuario();
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.of(usuario));
        given(snapshotRepository.findAllByUserName(usuario.getUserName(), PageRequest.of(2, 5)))
                .willReturn(List.of(new Snapshot(usuario), new Snapshot(usuario)));

        List<SnapshotDto> resultado = snapshotService.verHistorico(USER_NORMALIZADO, 3, 5);

        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get(0).informacoesAntigas().size());
    }

    @Test
    @DisplayName("verHistorico: retorna DTO com lista de informacoes vazia quando nao ha snapshots")
    void verHistorico_semSnapshots() {
        Usuario usuario = criarUsuario();
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.of(usuario));
        given(snapshotRepository.findAllByUserName(usuario.getUserName(), PageRequest.of(0, 30)))
                .willReturn(List.of());

        List<SnapshotDto> resultado = snapshotService.verHistorico(USER_NORMALIZADO, 1, 30);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).informacoesAntigas().isEmpty());
    }

    @Test
    @DisplayName("verHistorico: lanca NotFoundException quando o usuario nao esta no banco")
    void verHistorico_usuarioForaDoBanco() {
        given(usuarioRepository.findByUserNameIgnoreCase(USER_NORMALIZADO)).willReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> snapshotService.verHistorico(USER_NORMALIZADO, 1, 30));

        assertTrue(ex.getMessage().contains(USER_NORMALIZADO));
        then(snapshotRepository).shouldHaveNoInteractions();
    }
}