package br.GitHub.Analytics.service;

import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.dto.RepositorioDto;
import br.GitHub.Analytics.dto.UsuarioMultiplosDto;
import br.GitHub.Analytics.dto.UsuarioUnicoDto;
import br.GitHub.Analytics.dto.usuarioDados.UsuarioMultiplosDados;
import br.GitHub.Analytics.dto.usuarioDados.UsuarioUnicoDados;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Value("${github.token}")
    private String token;

    private final String URLUNICA = "https://api.github.com/";
    private final String URLGERAL = "https://api.github.com/search/";
    private final IUsuarioRepository usuarioRepository;
    private final RepositorioService repositorioService;
    private final RestClient restClient;

    public UsuarioUnicoDto buscarUsuario(String nome) {
        Optional<Usuario> usuario = usuarioRepository.findByUserNameIgnoreCase(nome);
        return usuario.map(UsuarioUnicoDto::new).orElseGet(() -> buscarUsuarioUnicoNaApi(nome));
    }

    public UsuarioUnicoDto buscarUsuarioUnicoNaApi(String nome) {
        nome = Utilitarios.formatarString(nome);

        UsuarioUnicoDados usuarioUnicoDados = restClient.get()
                .uri(URLUNICA + "users/{usuario}", nome)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(UsuarioUnicoDados.class);

        if(usuarioUnicoDados == null || Utilitarios.verificarCamposNull(usuarioUnicoDados)) {
            throw new NotFoundException(String.format("Nenhum usuário com o username '%s' foi encontrado.", nome));
        }

        List<RepositorioDto> repositorioDtos = repositorioService.buscarRepositorioNaApi(usuarioUnicoDados.repositoriosUrl());
        UsuarioUnicoDto usuarioUnicoDto = new UsuarioUnicoDto(usuarioUnicoDados, repositorioDtos);

        Usuario user = new Usuario(usuarioUnicoDto);
        repositorioDtos.forEach(r -> user.adicionarRepositorio(new Repositorio(r)));

        usuarioRepository.save(user);
        return usuarioUnicoDto;
    }

    public UsuarioMultiplosDto buscarUsuariosMultiplosNaApi(String nome) {
        nome = Utilitarios.formatarString(nome);

        UsuarioMultiplosDados usuarioMultiplosDados = restClient.get()
                .uri(URLGERAL + "users?q={nome}", nome)
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(UsuarioMultiplosDados.class);

        if(usuarioMultiplosDados == null || Utilitarios.verificarCamposNull(usuarioMultiplosDados)) {
            throw new NotFoundException(String.format("Nenhum usuário com o nome  %s foi encontrado.", nome));
        }

        return new UsuarioMultiplosDto(usuarioMultiplosDados.totalDeUsuarios(), usuarioMultiplosDados.items());
    }
}
