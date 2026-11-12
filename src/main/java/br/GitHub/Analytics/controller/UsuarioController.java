package br.GitHub.Analytics.controller;

import br.GitHub.Analytics.dto.usuario.*;
import br.GitHub.Analytics.service.UsuarioService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api.github/users")
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping("/unico")
    public ResponseEntity<UsuarioUnicoDto> buscarUsuarioUnico(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(service.buscarUsuario(nome));
    }

    @GetMapping("/multiplos")
    public ResponseEntity<UsuariosMultiplosDto> buscarUsuariosMultiplos(
            @RequestParam
            @Size(min = 1, max = 39, message = "Mínimo de 0 e máximo de 255 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(service.buscarUsuariosMultiplosNaApi(nome));
    }

    @GetMapping("/eventos")
    public ResponseEntity<List<UsuarioEventosDto>> buscarEventos(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(service.buscarEventosNaApi(nome));
    }

    @GetMapping("/contribuicoes")
    public ResponseEntity<UsuarioContribuicoesDto> buscarContribuicoes(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(service.buscarContribuicoes(nome));
    }

    @GetMapping("/seguidores")
    public ResponseEntity<UsuarioSeguidoresDto> buscarSeguidores(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(service.buscarSeguidoresNaApi(nome));
    }

    @GetMapping("/seguindo")
    public ResponseEntity<UsuarioSeguindoDto> buscarSeguindo(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(service.buscarSeguindoNaApi(nome));
    }
}
