package com.vjoon.se.core.repository;

import com.vjoon.se.core.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {

    List<Media> findByMediaId(String id);

    List<Media> findByExistsInProduction(boolean b);

    @Query("select m from Media m where m.existsInProduction = false and m.snapshots is empty")
    List<Media> findByNotExistsInProductionAndSnapshotsIsEmpty();
}
