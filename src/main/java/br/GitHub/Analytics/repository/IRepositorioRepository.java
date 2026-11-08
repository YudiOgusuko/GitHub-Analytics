package br.GitHub.Analytics.repository;

import br.GitHub.Analytics.domain.Repositorio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRepositorioRepository extends JpaRepository<Repositorio, Long> {
}
