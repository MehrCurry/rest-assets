package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.control.MediaController;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.repository.MediaRepository;
import com.wordnik.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Api(basePath = "/assets", value = "Assets", description = "Operations with Assets", produces = "application/json")
@RestController @Slf4j @RequestMapping(value = "/assets")public class AssetsResource {

    @Autowired private MediaController controller;

    @Autowired private MediaRepository repository;

    @RequestMapping(method = RequestMethod.POST) public @ResponseBody HttpEntity handleFileUpload(
            @RequestParam(value = "file", required = true) MultipartFile file,
            @RequestParam(value = "key", required = true) String ref,
            @RequestParam(value = "namespace", required = true) String nameSpace) throws IOException {

        controller.handleUpload(file, ref, nameSpace, false);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Media> findAll() {
        return repository.findAll();
    }

    @RequestMapping(method = RequestMethod.DELETE) public HttpEntity deleteAll() throws IOException {
        controller.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
