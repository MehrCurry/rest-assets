package de.gzockoll.prototype.assets.boundary;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import de.gzockoll.prototype.assets.entity.Asset;
import de.gzockoll.prototype.assets.entity.AssetDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
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
@RequestMapping("/assets")
@Slf4j
public class AssetResource {

    @Autowired
    private AssetDao dao;

    /**
     * Adds a document to the archive.
     *
     * Url: /archive/upload?file={file} [POST]
     *
     * @param file A file posted in a multipart request
     * @return The meta data of the added document
     */
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    String handleFileUpload(
            @RequestParam(value = "file", required = true) MultipartFile file) throws IOException {

        Asset asset=new Asset(file.getInputStream(),file.getOriginalFilename());
        return save(asset).orElseThrow(() -> new IllegalArgumentException("Duplicate key")).toString();
    }

    /**
     * Finds document in the archive. Returns a list of document meta data
     * which does not include the file data. Use getDocument to get the file.
     * Returns an empty list if no document was found.
     *
     * Url: /archive/documents [GET]
     *
     * @return A list of document meta data
     */
    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<List<String>> findAll() {
        HttpHeaders httpHeaders = new HttpHeaders();
        List<GridFSDBFile> results = dao.findAll();
        return new ResponseEntity<>(results.stream().map(f -> f.toString()).collect(Collectors.toList()), httpHeaders,HttpStatus.OK);
    }

    /**
     * Returns the document file from the archive with the given UUID.
     *
     * Url: /archive/document/{id} [GET]
     *
     * @param id The UUID of a document
     * @return The document file
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocument(@PathVariable String id) throws IOException {
        // send it back to the client
        Optional<GridFSDBFile> result = dao.findById(id);

        return streamResult(result);
    }

    @RequestMapping(method = RequestMethod.GET, params = "filename")
    public HttpEntity<InputStreamResource> findByFilename(@RequestParam(value = "filename") String filename) throws IOException {
        // send it back to the client
        Optional<GridFSDBFile> result = dao.findByKeyValue("filename", filename);

        return streamResult(result);
    }

    @RequestMapping(method = RequestMethod.DELETE, params = "filename")
    public HttpEntity<InputStreamResource> deleteByFilename(@RequestParam(value = "filename") String filename) throws IOException {
        // send it back to the client
        Optional<GridFSDBFile> result = dao.findByKeyValue("filename", filename);
        return deleteIfPresent(result);
    }

    private HttpEntity<InputStreamResource> streamResult(Optional<GridFSDBFile> result) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (result.isPresent()) {
            httpHeaders.setContentType(MediaType.valueOf(result.get().getContentType()));
            httpHeaders.setContentDispositionFormData("attachment", result.get().getFilename());
            httpHeaders.setContentLength(result.get().getLength());
            return new ResponseEntity<>(new InputStreamResource(result.get().getInputStream()), httpHeaders, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public HttpEntity deleteDocument(@PathVariable String id) throws IOException {
        checkArgument(id != null);
        Optional<GridFSDBFile> found = dao.findByKeyValue("_id", id);
        return deleteIfPresent(found);
    }

    private HttpEntity deleteIfPresent(Optional<GridFSDBFile> found) {
        if (found.isPresent()) {
            String id=found.get().getId().toString();
            dao.deleteByKeyValue("_id", id);
            log.debug(found.get().getFilename() + " deleted!");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    public void fileImport(Exchange ex) {
        File file= (File) ex.getIn().getBody(GenericFile.class).getFile();
        Asset asset=new Asset(file);
        save(asset);
    }

    Optional<GridFSFile> save(Asset asset) {
        Optional<GridFSDBFile> existing = dao.findByHash(asset.checksum());
        if (existing.isPresent()) {
            log.debug("File already existing: " + asset.getFilename() + " HASH: " + asset.checksum());
            return Optional.empty();
        } else {
            return Optional.of(dao.save(asset));
        }
    }

}
