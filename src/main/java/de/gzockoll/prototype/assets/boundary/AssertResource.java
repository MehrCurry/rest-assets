package de.gzockoll.prototype.assets.boundary;

import com.google.common.io.ByteStreams;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import de.gzockoll.prototype.assets.entity.Asset;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequestMapping("/assets")
@Slf4j
public class AssertResource {

    @Autowired
    private GridFsTemplate template;

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
    public HttpEntity<byte[]> getDocument(@PathVariable String id) throws IOException {
        // send it back to the client
        HttpHeaders httpHeaders = new HttpHeaders();
        GridFSDBFile result = template.findOne(query(where("_id").is(id)));
        if (result!=null) {
            httpHeaders.setContentType(MediaType.valueOf(result.getContentType()));
            return new ResponseEntity<>(ByteStreams.toByteArray(result.getInputStream()), httpHeaders, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
