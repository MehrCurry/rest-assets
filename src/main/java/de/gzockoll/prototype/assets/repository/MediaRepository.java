package de.gzockoll.prototype.assets.repository;

import de.gzockoll.prototype.assets.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media,Long> {
    List<Media> findByMediaId(String mediaId);

    List<Media> findByFilename(String filename);

    List<Media> findByExistsInProduction(boolean b);
}
