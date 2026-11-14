package br.GitHub.Analytics.repository;

import br.GitHub.Analytics.domain.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISnapshotRepository extends JpaRepository<Snapshot, Long> {

    List<Snapshot> findAllByUserName(String nome);
}
