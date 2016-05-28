package de.gzockoll.prototype.ams.repository;

import de.gzockoll.prototype.ams.entity.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {

    Stream<Snapshot> findByNamespace(String namespace);
}
