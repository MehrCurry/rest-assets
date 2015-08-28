package com.vjoon.se.core.boundary.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vjoon.se.core.control.SnapshotController;
import com.vjoon.se.core.entity.Snapshot;

@RestController
@Slf4j
@Api("Snapshot")
public class SnapshotResource {

    @Autowired
    private SnapshotController controller;

    @ApiOperation("Creates a Snapshot")
    @RequestMapping(value = "/snapshots", method = RequestMethod.POST)
    public HttpEntity create() {
        controller.create();
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation("Deletes the Snapshot with the given Id")
    @RequestMapping(value = "/snapshot/{id}", method = RequestMethod.DELETE)
    public HttpEntity delete(@PathVariable("id") @ApiParam(name = "id", value = "The Id of the snapshot to delete",
            required = true) Long id) {
        controller.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/snapshots", method = RequestMethod.DELETE)
    @ApiOperation("Deletes all Snapshots")
    public HttpEntity deleteAll() {
        controller.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(httpMethod = "GET", value = "Returns all Snapshots")
    @RequestMapping(value = "/snapshots", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<Snapshot> findAll() {
        return controller.findAll();
    }
}
