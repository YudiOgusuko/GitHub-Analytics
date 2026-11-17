package br.GitHub.Analytics.controller;

import br.GitHub.Analytics.dto.repositorio.RepositorioUnicoDto;
import br.GitHub.Analytics.dto.repositorio.RepositoriosMultiplosDto;
import br.GitHub.Analytics.service.RepositorioService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepositorioController.class)
class RepositorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepositorioService service;

    private final String request = "/api.github/repos";
    private final String usuario = "YudiOgusuko";

    @Test
    void buscarRepositorioMultiplos() throws Exception {

        List<RepositoriosMultiplosDto> repositoriosDto = new ArrayList<>();

        given(service.buscarRepositoriosMultiplos(usuario))
                .willReturn(repositoriosDto);

        mockMvc.perform(
                get(request + "/multiplos")
                        .param("usuario", usuario)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void buscarRepositorioUnico() throws Exception {

        String repositorio = "Previsao_do_Tempo";

        given(service.buscarRepositorioUnicoNaApi(usuario, repositorio))
                .willReturn(RepositorioUnicoDto.builder().build());

        mockMvc.perform(
                get(request + "/multiplos")
                        .param("usuario", usuario)
                        .param("repositorio", repositorio)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}