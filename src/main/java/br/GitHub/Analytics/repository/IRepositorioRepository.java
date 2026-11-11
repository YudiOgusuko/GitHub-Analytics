package br.GitHub.Analytics.repository;

import br.GitHub.Analytics.domain.Repositorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IRepositorioRepository extends JpaRepository<Repositorio, Long> {

    Optional<List<Repositorio>> findByusuarioId (Long id);

    Optional<List<Repositorio>> findByUsuarioId (Long id);

}
