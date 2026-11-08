package br.GitHub.Analytics.repository;

import br.GitHub.Analytics.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUserNameIgnoreCase(String nome);
}
