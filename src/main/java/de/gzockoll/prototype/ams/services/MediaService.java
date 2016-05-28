package de.gzockoll.prototype.ams.services;

import de.gzockoll.prototype.ams.entity.Asset;
import de.gzockoll.prototype.ams.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @Slf4j @Transactional

public class MediaService {

    @Autowired private AssetRepository repository;

    public List<Asset> getAll() {
        return repository.findAll();
    }
}
