package br.GitHub.Analytics.controller;

import br.GitHub.Analytics.dto.UsuarioMultiplosDto;
import br.GitHub.Analytics.dto.UsuarioUnicoDto;
import br.GitHub.Analytics.service.UsuarioService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api.github/users")
@Validated
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
    public ResponseEntity<UsuarioMultiplosDto> buscarUsuariosMultiplos(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 255 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(service.buscarUsuariosMultiplosNaApi(nome));
    }
}
