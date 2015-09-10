package com.vjoon.se.core.repository;

import com.vjoon.se.core.entity.NameSpace;
import com.vjoon.se.core.entity.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {

    List<Snapshot> findByNamespace(NameSpace namespace);
}
