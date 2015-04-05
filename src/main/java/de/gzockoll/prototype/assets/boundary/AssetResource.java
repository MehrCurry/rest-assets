package de.gzockoll.prototype.assets.boundary;

import com.google.common.io.ByteStreams;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import de.gzockoll.prototype.assets.entity.Asset;
import de.gzockoll.prototype.assets.entity.AssetDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

@RestController
@RequestMapping("/assets")
@Slf4j
public class AssetResource {

    @Autowired
    private GridFsTemplate template;

    @Autowired
    private AssetDao dao;

    public GridFSFile save(Asset a) {
        return template.store(a.asByteStream(),a.getFilename(),a.getMimeType());
    }

    /**
     * Adds a document to the archive.
     *
     * Url: /archive/upload?file={file} [POST]
     *
     * @param file A file posted in a multipart request
     * @return The meta data of the added document
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    String handleFileUpload(
            @RequestParam(value = "file", required = true) MultipartFile file) {

        try {
            Asset asset=new Asset(file.getInputStream(),file.getOriginalFilename());
            return save(asset).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        List<GridFSDBFile> results = template.find(null);
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
        HttpHeaders httpHeaders = new HttpHeaders();
        Optional<GridFSDBFile> result = dao.findById(id);

        if (result.isPresent()) {
            httpHeaders.setContentType(MediaType.valueOf(result.get().getContentType()));
            httpHeaders.setContentDispositionFormData("attachment",result.get().getFilename());
            httpHeaders.setContentLength(result.get().getLength());
            return new ResponseEntity<>(new InputStreamResource(result.get().getInputStream()), httpHeaders, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public void fileImport(Exchange ex) {
        File file= (File) ex.getIn().getBody(GenericFile.class).getFile();
        Asset asset=new Asset(file);
        Optional<GridFSDBFile> existing = dao.findByHash(asset.checksum());
        if (existing.isPresent()) {
            log.debug("File already existing: " + asset.getFilename() + " HASH: " + asset.checksum());
        } else {
            save(asset);
        }
    }

}
