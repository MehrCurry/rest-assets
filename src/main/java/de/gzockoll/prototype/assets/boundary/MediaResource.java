package de.gzockoll.prototype.assets.boundary;

import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/media")
@Slf4j
public class MediaResource {
    @Autowired
    private MediaRepository repository;

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    HttpEntity handleFileUpload(
            @RequestParam(value = "file", required = true) MultipartFile file) throws IOException {

        saveToInbox(file);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    private void saveToInbox(MultipartFile multipart) throws IOException {
        File convFile = new File("assets/inbox", multipart.getOriginalFilename());
        multipart.transferTo(convFile);
    }

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<List<String>> findAll() {
        HttpHeaders httpHeaders = new HttpHeaders();
        List<Media> results = repository.findAll();
        return new ResponseEntity<>(results.stream().map(f -> f.toString()).collect(Collectors.toList()), httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocument(@PathVariable String id) throws IOException {
        // send it back to the client
        Optional<Media> result = repository.findByMediaId(id).stream().findFirst();

        return streamResult(result);
    }

    private HttpEntity<InputStreamResource> streamResult(Optional<Media> result) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (result.isPresent()) {
            httpHeaders.setContentType(MediaType.valueOf(result.get().getContentType()));
            httpHeaders.setContentDispositionFormData("attachment", result.get().getOriginalFilename());
            httpHeaders.setContentLength(result.get().getLength());
            return new ResponseEntity<>(new InputStreamResource(result.get().getInputStream()), httpHeaders, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public HttpEntity deleteDocument(@PathVariable String id) throws IOException {
        checkArgument(id != null);
        Optional<Media> found = repository.findByMediaId(id).stream().findFirst();
        return deleteIfPresent(found);
    }

    @RequestMapping(value = "/all", method = RequestMethod.DELETE)
    public HttpEntity deleteAll() throws IOException {
        repository.findAll().forEach(m -> m.deleteFiles());
        repository.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private HttpEntity deleteIfPresent(Optional<Media> found) {
        if (found.isPresent()) {
            Media m=found.get();
            m.deleteFromProduction();
            repository.save(m);
            log.debug(found.get().getFilename() + " removed!");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
