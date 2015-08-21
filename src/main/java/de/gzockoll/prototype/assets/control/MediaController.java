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

import static com.google.common.base.Preconditions.checkArgument;

@Service
@Transactional
public class MediaController {
    @Autowired
    private MediaRepository repository;

    @EndpointInject(uri="direct:production")
    private ProducerTemplate producerTemplate;



    public void handleUpload(MultipartFile multipart, String ref, String nameSpace) throws IOException {
        checkArgument(multipart!=null);
        checkArgument(nameSpace!=null);

        Media media=Media.builder()
                .length(multipart.getSize())
                .contentType(multipart.getContentType())
                .nameSpace(nameSpace)
                .originalFilename(multipart.getOriginalFilename())
                .build();
        media.setExternalReference(ref);
        File convFile = new File(media.getFullname());
        convFile.getParentFile().mkdirs();
        multipart.transferTo(convFile.getAbsoluteFile());
        media.extractInfosFromFile(convFile);
        media.setExistsInProduction(true);
        repository.save(media);
    }
}
