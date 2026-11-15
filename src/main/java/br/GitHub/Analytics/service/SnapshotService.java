package br.GitHub.Analytics.service;

import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.domain.Snapshot;
import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.dto.snapshot.SnapshotDto;
import br.GitHub.Analytics.dto.snapshot.informacoes.InformacoesAntigas;
import br.GitHub.Analytics.dto.snapshot.resultadoRepositorio.ResultadoRepositorios;
import br.GitHub.Analytics.dto.usuario.UsuarioContribuicoesDto;
import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import br.GitHub.Analytics.dto.usuario.dados.UsuarioUnicoDados;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IRepositorioRepository;
import br.GitHub.Analytics.repository.ISnapshotRepository;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SnapshotService {

    @Value("${github.token}")
    private String token;

    private final String URL = "https://api.github.com/users/";
    private final IUsuarioRepository usuarioRepository;
    private final IRepositorioRepository repositorioRepository;
    private final ISnapshotRepository snapshotRepository;
    private final RepositorioService repositorioService;
    private final UsuarioService usuarioService;
    private final SincronizacaoPersistenciaService sincronizacaoPersistenciaService;
    private final RestClient restClient;

    public List<SnapshotDto> verHistorico(String nome) {
        String nomeFormatado = Utilitarios.tirarEspaco(nome);

        Usuario usuario = usuarioRepository.findByUserNameIgnoreCase(nome)
                .orElseThrow(() -> new NotFoundException(String.format("'%s' não esta no Banco de Dados. Não é possível acessar o histórico.", nomeFormatado)));

        List<Snapshot> snapshots = snapshotRepository.findAllByUserName(usuario.getUserName());
        List<SnapshotDto> snapshotsDto = new ArrayList<>();
        List<InformacoesAntigas> informacoesAntigas = new ArrayList<>();

        snapshots.forEach(s -> informacoesAntigas.add(new InformacoesAntigas(s)));
        snapshotsDto.add(new SnapshotDto(usuario.getUserName(), informacoesAntigas));

        return snapshotsDto.stream()
                .sorted(Comparator.comparing((SnapshotDto s) -> Utilitarios.formatarDataRepositorio(s.informacoesAntigas().get(0).sincronizadoEm())).reversed())
                .toList();
    }

    public UsuarioUnicoDto sincronizarDadosUsuarioERepositorios(String nome) {
        nome = Utilitarios.tirarEspaco(nome);
        String nomeParametro = nome;

        Usuario usuarioBanco = usuarioRepository.findByUserNameIgnoreCase(nome)
                .orElseThrow(() -> new NotFoundException(String.format("O usuário '%s' não esta no Banco de Dados. Nenhum dado foi alterado.", nomeParametro)));

        UsuarioUnicoDados usuarioUnicoDados = usarRestClient(usuarioBanco.getUserName());

        if (usuarioUnicoDados == null) throw new NotFoundException(String.format("Nenhum usuário com o username '%s' foi encontrado.", nome));

        List<RepositoriosMultiplosDto> repositorioDaApi = repositorioService.buscarRepositoriosMultiplosNaApi(usuarioBanco.getUserName());
        UsuarioContribuicoesDto contribuicoesDto = usuarioService.buscarContribuicoesNaApi(nome);
        UsuarioUnicoDto usuarioUnicoDto = new UsuarioUnicoDto(usuarioUnicoDados, repositorioDaApi, contribuicoesDto);
        List<Repositorio> repositorio = repositorioRepository.findAllByUsuarioId(usuarioBanco.getId());

        int repositoriosAntes = usuarioBanco.getRepositorios();

        ResultadoRepositorios resultadoRepositorios = Repositorio.temDadosDiferentes(usuarioBanco, repositorioDaApi, repositorioRepository);
        Boolean dadosUsuarioAlterado = usuarioBanco.temDadosDiferentes(usuarioBanco, usuarioUnicoDto);

        Snapshot snapshot = new Snapshot(usuarioBanco);
        snapshot.tabelaUsuarioFoiAlterada(dadosUsuarioAlterado);
        snapshot.repositoriosAlterados(resultadoRepositorios, repositoriosAntes, usuarioUnicoDados.totalRepositorios());
        snapshot.tabelaRepositorioFoiAlterada(resultadoRepositorios);

        usuarioBanco.adicionarSnapshot(snapshot);
        sincronizacaoPersistenciaService.salvar(usuarioBanco, repositorio );

        return usuarioUnicoDto;
    }

    private UsuarioUnicoDados usarRestClient(String uri) {
        return restClient.get()
                .uri(URL + uri)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(UsuarioUnicoDados.class);
    }
}
