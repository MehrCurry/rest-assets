package de.gzockoll.prototype.ams.repository;

import de.gzockoll.prototype.ams.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.stream.Stream;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByMediaId(String id);

    List<Asset> findByExistsInProduction(boolean b);

    @Query("select a from Asset a where a.existsInProduction = false and a.snapshots is empty")
    List<Asset> findByNotExistsInProductionAndSnapshotsIsEmpty();

    List<Asset> findByNameSpaceAndKey(String namespace, String key);

    Stream<Asset> findByNameSpaceAndExistsInProduction(String namespace, boolean b);
}
