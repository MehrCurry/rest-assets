package de.gzockoll.prototype.assets.services;

import com.google.common.base.Stopwatch;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.entity.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws.s3.S3Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class MediaService {
    @Autowired
    private MediaRepository repository;

    public Media createMediaInfo(Exchange ex) {
        Media media=new Media(ex.getIn().getHeader("CamelFileName").toString());
        media.setHash(checksum(ex.getIn().getBody(File.class)));
        repository.save(media);
        return media;
    }

    public void finishS3Transfer(Exchange ex) {
        Media media= (Media) ex.getIn().getHeader("media");
        media=repository.findOne(media.getId());
        media.addLocation("s3", ex.getIn().getHeader(S3Constants.KEY).toString());
        repository.save(media);
    }

    public void finishGridFsTransfer(Exchange ex) {
        Media media= (Media) ex.getIn().getHeader("media");
        media=repository.findOne(media.getId());
        media.addLocation("gridfs",media.getFilename());
        repository.save(media);
    }

    private String checksum(File file) {
        Stopwatch sw=Stopwatch.createStarted();
        HashCode hash = null;
        try {
            hash = Files.hash(file, Hashing.md5());
            sw.stop();
            log.debug("Hash took " + sw.toString());
            log.debug("Hash for {} is {}", file.getName(), hash);
            return hash.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(file.toString());
        }
    }

    public Media findMedidInfo(Exchange ex) {
        return repository.findByMediaId(ex.getIn().getHeader("CamelFileName").toString()).stream().findFirst().orElse(null);
    }

    public List<Media> getAll() {
        return repository.findAll();
    }
}
