package br.GitHub.Analytics.dto.usuario.linguagensMaisUsadas;

import lombok.Builder;

import java.util.List;

@Builder
public record LinguagensMaisUsadas(String userName,
                                   List<String> listaLinguagens) {

}
