package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.control.SnapshotController;
import com.vjoon.se.core.entity.Snapshot;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(basePath = "/snapshots", value = "Snapshots", description = "Operations with Snapshots", produces = "application/json")
@RestController @Slf4j @RequestMapping(value = "/snapshots") public class SnapshotsResource {

    @Autowired private SnapshotController controller;

    @ApiOperation(value = "creates a snapshot",
            notes = "All assets currently in the production area will be part of that snapshot")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "") })
    @RequestMapping(method = RequestMethod.POST) public HttpEntity create() {
        controller.create();
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Removes all snapshots",
            notes = "Use with extreme care")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "") })
    @RequestMapping(method = RequestMethod.DELETE) public HttpEntity deleteAll() {
        controller.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Retrieve all snapshots")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all snapshots") })
    @RequestMapping(method = RequestMethod.GET, produces = "application/json") public @ResponseBody
    List<Snapshot> findAll() {
        return controller.findAll();
    }
}
