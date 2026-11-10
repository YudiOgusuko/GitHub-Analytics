package br.GitHub.Analytics.dto.usuario.contribuicoes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Contribuicoes(Data data,
                            List<Map<String, Object>> errors) {
}
