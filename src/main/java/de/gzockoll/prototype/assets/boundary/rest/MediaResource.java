package de.gzockoll.prototype.assets.boundary.rest;

import de.gzockoll.prototype.assets.boundary.StreamHelper;
import de.gzockoll.prototype.assets.control.MediaController;
import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@Slf4j
public class MediaResource {
    @Autowired
    private MediaController controller;

    @Autowired
    private MediaRepository repository;

    @RequestMapping(value= "/assets", method = RequestMethod.POST)
    public @ResponseBody
    HttpEntity handleFileUpload(
            @RequestParam(value = "file", required = true) MultipartFile file,
            @RequestParam(value = "key", required = false) String ref,
            @RequestParam(value = "namespace", required = true) String nameSpace
            ) throws IOException {

        controller.handleUpload(file, ref, nameSpace);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    private void saveToInbox(MultipartFile multipart) throws IOException {
        File convFile = new File("assets/inbox", multipart.getOriginalFilename());
        multipart.transferTo(convFile);
    }

    @RequestMapping(value= "/assets", method = RequestMethod.GET, produces = "application/json")
    public List<Media> findAll() {
        return repository.findAll();
    }

    @RequestMapping(value = "/asset/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocument(@PathVariable String id) throws IOException {
        // send it back to the client
        Optional<Media> result = repository.findByMediaId(id).stream().findFirst();

        return StreamHelper.streamResult(result.orElseThrow(() -> new NoSuchElementException(id)));
    }


    @RequestMapping(value = "/asset/{id}", method = RequestMethod.DELETE)
    public HttpEntity deleteDocument(@PathVariable String id) throws IOException {
        checkArgument(id != null);
        controller.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/assets", method = RequestMethod.DELETE)
    public HttpEntity deleteAll() throws IOException {
        controller.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
