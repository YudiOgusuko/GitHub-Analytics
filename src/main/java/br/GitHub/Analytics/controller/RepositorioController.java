package br.GitHub.Analytics.controller;

import br.GitHub.Analytics.dto.repositorio.RepositorioUnicoDto;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.service.RepositorioService;
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
@RequestMapping("/api.github/repos")
@RequiredArgsConstructor
public class RepositorioController {

    private final RepositorioService service;

    @GetMapping("/multiplos")
    public ResponseEntity<List<RepositoriosMultiplosDto>> buscarRepositorioMultiplos(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String usuario){
        return ResponseEntity.ok().body(service.buscarRepositoriosMultiplos(usuario));
    }

    @GetMapping("/unico")
    public ResponseEntity<RepositorioUnicoDto> buscarRepositorioUnico(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String usuario,
            @RequestParam
            @NotBlank(message = "O nome do repositório é obrigatório.")
            @Size(min = 1, max = 100, message = "Mínimo de 1 e máximo de 100 caracteres.")
            String repositorio){
        return ResponseEntity.ok().body(service.buscarRepositorioUnicoNaApi(usuario, repositorio));
    }
}
