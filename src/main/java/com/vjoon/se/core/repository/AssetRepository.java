package com.vjoon.se.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vjoon.se.core.entity.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByExistsInProduction(boolean b);

    List<Asset> findByMediaId(String id);

    @Query("select a from Asset a where a.existsInProduction = false and a.snapshots is empty")
    List<Asset> findByNotExistsInProductionAndSnapshotsIsEmpty();

    List<Asset> findByNameSpaceAndKey(String namespace, String key);
}
