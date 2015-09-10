package com.vjoon.se.core.repository;

import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.entity.NameSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByExistsInProduction(boolean b);

    List<Asset> findByMediaId(String id);

    @Query("select a from Asset a where a.existsInProduction = false and a.snapshots is empty")
    List<Asset> findByNotExistsInProductionAndSnapshotsIsEmpty();

    List<Asset> findByNameSpaceAndKey(NameSpace namespace, String key);

    List<Asset> findByNameSpaceAndExistsInProduction(NameSpace namespace, boolean b);
}
