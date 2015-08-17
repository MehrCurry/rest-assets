package de.gzockoll.prototype.assets.repository;

import de.gzockoll.prototype.assets.entity.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<Snapshot,Long>{
}
