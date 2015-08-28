package com.vjoon.se.core.boundary.rest;

import static com.google.common.base.Preconditions.checkArgument;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vjoon.se.core.boundary.StreamHelper;
import com.vjoon.se.core.control.MediaController;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.repository.MediaRepository;

@RestController
@Slf4j
@Api("Media Resources")
public class MediaResource {

    @Autowired
    private MediaController controller;

    @Autowired
    private MediaRepository repository;

    @Autowired
    private StreamHelper streamHelper;

    @ApiOperation("Delete all Assets")
    @RequestMapping(value = "/assets", method = RequestMethod.DELETE)
    public HttpEntity deleteAll() throws IOException {
        controller.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("Delete the Asset with the given Id")
    @RequestMapping(value = "/asset/{id}", method = RequestMethod.DELETE)
    public HttpEntity deleteDocument(
            @ApiParam(name = "id", required = true, value = "Id of the document to delete") @PathVariable String id)
            throws IOException {
        checkArgument(id != null);
        controller.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("Find all Assets")
    @RequestMapping(value = "/assets", method = RequestMethod.GET, produces = "application/json")
    public List<Media> findAll() {
        return repository.findAll();
    }

    @ApiOperation("Uploades a File")
    @RequestMapping(value = "/assets", method = RequestMethod.POST)
    public @ResponseBody HttpEntity handleFileUpload(@RequestParam(value = "file", required = true) @ApiParam(
            name = "file", required = true, value = "File to upload") MultipartFile file, @RequestParam(value = "key",
            required = true) String ref, @RequestParam(value = "namespace", required = true) String nameSpace)
            throws IOException {

        controller.handleUpload(file, ref, nameSpace, false);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
