package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.control.SnapshotController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(basePath = "/snapshot", value = "Snapshot", description = "Operations with a single Snapshots", produces = "application/json")
@RestController @Slf4j @RequestMapping(value = "/snapshot") public class SnapshotResource {

    @Autowired
    private SnapshotController controller;

    @ApiOperation("Deletes the Snapshot with the given Id")
    @RequestMapping(value = "/snapshot/{id}", method = RequestMethod.DELETE)
    public HttpEntity delete(@PathVariable("id") Long id) {
        controller.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
