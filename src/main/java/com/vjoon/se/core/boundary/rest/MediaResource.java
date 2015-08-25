package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.boundary.StreamHelper;
import com.vjoon.se.core.control.MediaController;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@RestController @Slf4j public class MediaResource {

    @Autowired private MediaController controller;

    @Autowired private MediaRepository repository;

    @Autowired private StreamHelper streamHelper;

    @RequestMapping(value = "/assets", method = RequestMethod.POST) public @ResponseBody HttpEntity handleFileUpload(
            @RequestParam(value = "file", required = true) MultipartFile file,
            @RequestParam(value = "key", required = true) String ref,
            @RequestParam(value = "namespace", required = true) String nameSpace) throws IOException {

        controller.handleUpload(file, ref, nameSpace, false);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/assets", method = RequestMethod.GET, produces = "application/json")
    public List<Media> findAll() {
        return repository.findAll();
    }

    @RequestMapping(value = "/asset/{id}", method = RequestMethod.DELETE)
    public HttpEntity deleteDocument(@PathVariable String id) throws IOException {
        checkArgument(id != null);
        controller.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/assets", method = RequestMethod.DELETE) public HttpEntity deleteAll() throws IOException {
        controller.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
