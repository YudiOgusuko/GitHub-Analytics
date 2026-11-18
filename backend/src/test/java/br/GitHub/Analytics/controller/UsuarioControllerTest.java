package br.GitHub.Analytics.controller;

import br.GitHub.Analytics.dto.paginacao.PaginaDto;
import br.GitHub.Analytics.dto.usuario.UsuarioContribuicoesDto;
import br.GitHub.Analytics.dto.usuario.UsuarioEventosDto;
import br.GitHub.Analytics.dto.usuario.UsuarioNetworkingDto;
import br.GitHub.Analytics.dto.usuario.UsuarioUnicoDto;
import br.GitHub.Analytics.dto.usuario.linguagensMaisUsadas.LinguagensMaisUsadas;
import br.GitHub.Analytics.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService service;

    private final String request = "/api.github/users";
    private final String nome = "YudiOgusuko";

    private final PaginaDto<UsuarioNetworkingDto> dto = PaginaDto.<UsuarioNetworkingDto>builder().build();

    @Test
    void buscarUsuarioUnico() throws Exception {

        given(service.buscarUsuario(nome))
                .willReturn(UsuarioUnicoDto.builder().build());

        mockMvc.perform(
                get(request + "/unico")
                        .param("nome", nome)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void buscarUsuariosMultiplos() throws Exception {

        given(service.buscarUsuariosMultiplosNaApi(nome, 1, 30))
                .willReturn(dto);

        mockMvc.perform(
                get(request + "/multiplos")
                        .param("nome", nome)
                        .param("pagina", "1")
                        .param("tamanho", "30")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void buscarEventos() throws Exception {

        PaginaDto<UsuarioEventosDto> eventosDto = PaginaDto.<UsuarioEventosDto>builder().build();

        given(service.buscarEventosNaApi(nome, 1, 30))
                .willReturn(eventosDto);

        mockMvc.perform(
                get(request + "/eventos")
                        .param("nome", nome)
                        .param("pagina", "1")
                        .param("tamanho", "30")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void buscarContribuicoes() throws Exception {

        given(service.buscarContribuicoes(nome))
                .willReturn(UsuarioContribuicoesDto.builder().build());

        mockMvc.perform(
                get(request + "/contribuicoes")
                        .param("nome", nome)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void buscarSeguidores() throws Exception {

        given(service.buscarSeguidoresNaApi(nome, 1, 30))
                .willReturn(dto);

        mockMvc.perform(
                get(request + "/seguidores")
                        .param("nome", nome)
                        .param("pagina", "1")
                        .param("tamanho", "30")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void buscarSeguindo() throws Exception {

        given(service.buscarSeguindoNaApi(nome, 1, 30))
                .willReturn(dto);

        mockMvc.perform(
                get(request + "/seguindo")
                        .param("nome", nome)
                        .param("pagina", "1")
                        .param("tamanho", "30")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void rankingLinguagens() throws Exception {

        given(service.rankingLinguagens(nome))
                .willReturn(LinguagensMaisUsadas.builder().build());

        mockMvc.perform(
                get(request + "/rankingLinguagens")
                        .param("nome", nome)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}