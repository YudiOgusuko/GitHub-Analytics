package br.GitHub.Analytics.dto.snapshot;

import br.GitHub.Analytics.dto.snapshot.informacoes.InformacoesAntigas;

import java.util.List;

public record SnapshotDto(String userName,
                          List<InformacoesAntigas> informacoesAntigas) {
}
