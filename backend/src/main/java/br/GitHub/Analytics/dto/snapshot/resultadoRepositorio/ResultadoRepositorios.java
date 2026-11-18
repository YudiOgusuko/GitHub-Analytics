package br.GitHub.Analytics.dto.snapshot.resultadoRepositorio;

public record ResultadoRepositorios(int adicionados, int atualizados){

    public boolean houveAlteracao() {
        return adicionados > 0 || atualizados > 0;
    }
}
