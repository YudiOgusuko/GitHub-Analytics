package br.GitHub.Analytics.repository;

import br.GitHub.Analytics.domain.Repositorio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IRepositorioRepository extends JpaRepository<Repositorio, Long> {

    @EntityGraph(attributePaths = "topicos")
    List<Repositorio> findAllByUsuarioId (Long id);

}
