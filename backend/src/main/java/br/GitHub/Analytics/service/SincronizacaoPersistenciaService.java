package br.GitHub.Analytics.service;

import br.GitHub.Analytics.domain.Repositorio;
import br.GitHub.Analytics.domain.Usuario;
import br.GitHub.Analytics.repository.IRepositorioRepository;
import br.GitHub.Analytics.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SincronizacaoPersistenciaService {

    private final IUsuarioRepository usuarioRepository;
    private final IRepositorioRepository repositorioRepository;

    @Transactional
    public void salvar(Usuario usuario, List<Repositorio> repositorios) {
        usuarioRepository.save(usuario);
        repositorioRepository.saveAll(repositorios);
    }
}
