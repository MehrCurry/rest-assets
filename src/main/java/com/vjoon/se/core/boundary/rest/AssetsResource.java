package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.control.MediaController;
import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.repository.AssetRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @Autowired private AssetRepository repository;

    @ApiOperation(value = "Creates an Asset from an multipart file upload",
            notes = "The database entry will only be set to existsInProduction=false")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "") })
    @RequestMapping(method = RequestMethod.POST) public @ResponseBody HttpEntity handleFileUpload(
            @RequestParam(value = "file", required = true) MultipartFile file,
            @RequestParam(value = "key", required = true) String ref,
            @RequestParam(value = "namespace", required = true) String nameSpace) throws IOException {

        controller.handleUpload(file, ref, nameSpace, false);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Returns all known assets",
            notes = "The database entry will only be set to existsInProduction=false")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of all assets") })
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Asset> findAll() {
        return repository.findAll();
    }

    @ApiOperation(value = "Removes all assets from the production area. Datebase Entries will be update.",
            notes = "If the asset is not part of any snapshot, it will be completly delete by the garbage collector")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "") })
    @RequestMapping(method = RequestMethod.DELETE) public HttpEntity deleteAll() throws IOException {
        controller.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
