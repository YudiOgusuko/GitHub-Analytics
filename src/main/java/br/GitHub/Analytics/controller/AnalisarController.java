package br.GitHub.Analytics.controller;

import br.GitHub.Analytics.dto.snapshot.SnapshotDto;
import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import br.GitHub.Analytics.service.SnapshotService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api.github/analisar")
public class AnalisarController {

    public final SnapshotService snapshotService;

    @PostMapping("/sincronizar")
    public ResponseEntity<UsuarioUnicoDto> sincronizarUsuario(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(snapshotService.sincronizarDadosUsuarioERepositorios(nome));
    }

    @GetMapping ("/historico")
    public ResponseEntity<List<SnapshotDto>> verHistorico(
            @RequestParam
            @NotBlank(message = "O nome do usuário é obrigatório.")
            @Size(min = 1, max = 39, message = "Mínimo de 1 e máximo de 39 caracteres.")
            String nome) {
        return ResponseEntity.ok().body(snapshotService.verHistorico(nome));
    }
}
