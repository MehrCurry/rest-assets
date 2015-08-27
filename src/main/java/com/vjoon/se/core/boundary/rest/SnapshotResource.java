package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.control.SnapshotController;
import com.vjoon.se.core.entity.Snapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @Slf4j public class SnapshotResource {

    @Autowired private SnapshotController controller;

    @RequestMapping(value = "/snapshots", method = RequestMethod.POST) public HttpEntity create() {
        controller.create();
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/snapshots", method = RequestMethod.DELETE) public HttpEntity deleteAll() {
        controller.deleteAll();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/snapshots", method = RequestMethod.GET, produces = "application/json") public @ResponseBody
    List<Snapshot> findAll() {
        return controller.findAll();
    }


    @RequestMapping(value = "/snapshot/{id}", method = RequestMethod.DELETE) public HttpEntity delete(@PathVariable("id") Long id) {
        controller.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
