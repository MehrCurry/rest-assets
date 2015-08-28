package com.vjoon.se.core.repository;

import com.vjoon.se.core.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByMediaId(String id);

    List<Asset> findByExistsInProduction(boolean b);

    @Query("select a from Asset a where a.existsInProduction = false and a.snapshots is empty")
    List<Asset> findByNotExistsInProductionAndSnapshotsIsEmpty();
}
