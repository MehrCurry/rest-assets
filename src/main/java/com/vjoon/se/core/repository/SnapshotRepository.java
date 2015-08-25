package com.vjoon.se.core.repository;

import com.vjoon.se.core.entity.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {

}
