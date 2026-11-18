package br.GitHub.Analytics.controller;

import br.GitHub.Analytics.dto.snapshot.SnapshotDto;
import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import br.GitHub.Analytics.service.SnapshotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalisarController.class)
class AnalisarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SnapshotService service;

    private final String request = "/api.github/analisar";
    private final String nome = "YudiOgusuko";

    @Test
    void sincronizarUsuario() throws Exception {

        given(service.sincronizarDadosUsuarioERepositorios(nome))
                .willReturn(UsuarioUnicoDto.builder().build());

        mockMvc.perform(
                post(request + "/sincronizar")
                        .param("nome", nome)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    void verHistoricoDeMudancaDeDados() throws Exception {

      List<SnapshotDto> snapshotDto = new ArrayList<>();

        given(service.verHistorico(nome, 1, 30))
                .willReturn(snapshotDto);

        mockMvc.perform(
                get(request + "/historico")
                        .param("nome", nome)
                        .param("pagina", "1")
                        .param("tamanho", "30")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}