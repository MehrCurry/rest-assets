package de.gzockoll.prototype.assets.boundary;

import com.google.common.base.Stopwatch;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import de.gzockoll.prototype.assets.entity.Asset;
import de.gzockoll.prototype.assets.entity.AssetDao;
import de.gzockoll.prototype.assets.entity.Token;
import de.gzockoll.prototype.assets.entity.TokenRepository;
import de.gzockoll.prototype.assets.services.MetaDataExtractorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/assets")
@Slf4j
public class AssetResource {

    @Autowired
    private AssetDao dao;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private MetaDataExtractorFactory factory;

    /**
     * Adds a document to the archive.
     *
     * Url: /archive/upload?file={file} [POST]
     *
     * @param file A file posted in a multipart request
     * @return The meta data of the added document
     */
    @CacheEvict(value = "assets",allEntries = true)
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    String handleFileUpload(
            @RequestParam(value = "file", required = true) MultipartFile file) throws IOException {

        Stopwatch sw=Stopwatch.createStarted();
        final File tmp = multipartToFile(file);
        try {
            Asset asset=new Asset(tmp,factory);
            String result = save(asset).orElseThrow(() -> new IllegalArgumentException("Duplicate key")).toString();
            sw.stop();
            log.debug("Import took " + sw.toString());
            log.debug("Speed: {} KB/s", new DecimalFormat("###.###").format(asset.sizeInKB() * 1000 / sw.elapsed(TimeUnit.MILLISECONDS)));
            return result;
        } finally {
            FileUtils.deleteQuietly(tmp);
        }
    }

    public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException
    {
        String tmpDir = System.getProperty("java.io.tmpdir");
        File convFile = new File(tmpDir, multipart.getOriginalFilename());
        multipart.transferTo(convFile);
        return convFile;
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
    @Cacheable("assets")
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
    @Cacheable(value = "assets",key = "#id")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HttpEntity<InputStreamResource> getDocument(@PathVariable String id) throws IOException {
        // send it back to the client
        Optional<GridFSDBFile> result = dao.findById(id);

        return streamResult(result);
    }

    @Cacheable(value = "assets",key = "#filename")
    @RequestMapping(method = RequestMethod.GET, params = "filename")
    public HttpEntity<InputStreamResource> findByFilename(@RequestParam(value = "filename") String filename) throws IOException {
        // send it back to the client
        Optional<GridFSDBFile> result = dao.findByKeyValue("filename", filename);

        return streamResult(result);
    }

    @RequestMapping(method = RequestMethod.GET, params = "token")
    public HttpEntity<InputStreamResource> findByToken(@RequestParam(value = "token") String tokenId) throws IOException {
        Token token=tokenRepository.findOne(tokenId);
        if (token !=null) {
            Optional<GridFSDBFile> result = dao.findById(token.getAssetId());

            return streamResult(result);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CacheEvict(value = "assets",allEntries = true)
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

    @CacheEvict(value = "assets",allEntries = true)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public HttpEntity deleteDocument(@PathVariable String id) throws IOException {
        checkArgument(id != null);
        Optional<GridFSDBFile> found = dao.findByKeyValue("_id", id);
        return deleteIfPresent(found);
    }

    @CacheEvict(value = "assets",allEntries = true)
    @RequestMapping(value = "/all", method = RequestMethod.DELETE)
    public HttpEntity deleteAll() throws IOException {
        dao.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
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

    @CacheEvict(value = "assets",allEntries = true)
    public void fileImport(Exchange ex) {
        Stopwatch sw=Stopwatch.createStarted();
        File file= (File) ex.getIn().getBody(GenericFile.class).getFile();
        Asset asset=new Asset(file,factory);
        save(asset);
        sw.stop();
        log.debug("Import took " + sw.toString());
        log.debug("Speed: {} KB/s", new DecimalFormat("###.###").format(asset.sizeInKB() * 1000 / sw.elapsed(TimeUnit.MILLISECONDS)));
        try {
            asset.checksum();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Optional<GridFSFile> save(Asset asset) {
        return Optional.of(dao.save(asset));
    }

}
