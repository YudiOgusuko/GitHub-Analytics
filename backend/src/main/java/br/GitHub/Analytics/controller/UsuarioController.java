package br.GitHub.Analytics.controller;

import br.GitHub.Analytics.dto.paginacao.PaginaDto;
import br.GitHub.Analytics.dto.usuario.UsuarioContribuicoesDto;
import br.GitHub.Analytics.dto.usuario.UsuarioEventosDto;
import br.GitHub.Analytics.dto.usuario.UsuarioNetworkingDto;
import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import br.GitHub.Analytics.dto.usuario.linguagensMaisUsadas.LinguagensMaisUsadas;
import br.GitHub.Analytics.service.UsuarioService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    public ResponseEntity<PaginaDto<UsuarioNetworkingDto>> buscarUsuariosMultiplos(
            @RequestParam
            @Size(min = 1, max = 255, message = "Mínimo de 0 e máximo de 255 caracteres.")
            String nome,
            @RequestParam(defaultValue = "1") @Min(1) Integer pagina,
            @RequestParam(defaultValue = "30") @Min(1) @Max(100) Integer tamanho) {
        return ResponseEntity.ok().body(service.buscarUsuariosMultiplosNaApi(nome, pagina, tamanho));
    }

    @GetMapping("/eventos")
    public ResponseEntity<PaginaDto<UsuarioEventosDto>> buscarEventos(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome,
            @RequestParam(defaultValue = "1") @Min(1) Integer pagina,
            @RequestParam(defaultValue = "30") @Min(1) @Max(100) Integer tamanho) {
        return ResponseEntity.ok().body(service.buscarEventosNaApi(nome, pagina, tamanho));
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
    public ResponseEntity<PaginaDto<UsuarioNetworkingDto>> buscarSeguidores(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome,
            @RequestParam(defaultValue = "1") @Min(1) Integer pagina,
            @RequestParam(defaultValue = "30") @Min(1) @Max(100) Integer tamanho) {
        return ResponseEntity.ok().body(service.buscarSeguidoresNaApi(nome, pagina, tamanho));
    }

    @GetMapping("/seguindo")
    public ResponseEntity<PaginaDto<UsuarioNetworkingDto>> buscarSeguindo(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome,
            @RequestParam(defaultValue = "1") @Min(1) Integer pagina,
            @RequestParam(defaultValue = "30") @Min(1) @Max(100) Integer tamanho) {
        return ResponseEntity.ok().body(service.buscarSeguindoNaApi(nome, pagina, tamanho));
    }

    @GetMapping("/rankingLinguagens")
    public ResponseEntity<LinguagensMaisUsadas> rankingLinguagens(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(service.rankingLinguagens(nome));
    }
}
