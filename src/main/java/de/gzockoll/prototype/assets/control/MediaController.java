package de.gzockoll.prototype.assets.control;

import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class MediaController {
    @Autowired
    private MediaRepository repository;

    @EndpointInject(uri="direct:production")
    private ProducerTemplate producerTemplate;



    public void upload(MultipartFile multipart) throws IOException {

        Media media=Media.builder()
                .length(multipart.getSize())
                .contentType(multipart.getContentType())
                .originalFilename(multipart.getOriginalFilename())
                .build();
        File convFile = new File("assets/production", media.generateFullname());
        convFile.getParentFile().mkdirs();
        multipart.transferTo(convFile.getAbsoluteFile());
        media.extractInfosFromFile(convFile);
        repository.save(media);
    }
}
