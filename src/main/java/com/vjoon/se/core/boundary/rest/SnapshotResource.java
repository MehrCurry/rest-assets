package com.vjoon.se.core.boundary.rest;

import com.vjoon.se.core.control.SnapshotController;
import com.vjoon.se.core.entity.Snapshot;
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

@RestController @RequestMapping("/snapshots") @Slf4j public class SnapshotResource {

    @Autowired private SnapshotController controller;

    @RequestMapping(method = RequestMethod.POST) public HttpEntity create() {
        controller.create();
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json") public @ResponseBody
    List<Snapshot> findAll() {
        return controller.findAll();
    }

}