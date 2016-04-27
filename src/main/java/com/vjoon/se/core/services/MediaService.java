package com.vjoon.se.core.services;

import com.vjoon.se.core.control.AssetController;
import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.entity.NameSpace;
import com.vjoon.se.core.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service @Slf4j @Transactional

public class MediaService {

    @Autowired private AssetRepository repository;
    @Autowired private AssetController assetController;

    public List<Asset> getAll() {
        return repository.findAll();
    }

    public void uploadAsset(@Body InputStream inputStream,@Header("namespace") String namespace,@Header("CamelFileName") String filename) throws IOException {
        assetController.saveAsset(inputStream,filename,new NameSpace(namespace),filename,false);
    }
}
