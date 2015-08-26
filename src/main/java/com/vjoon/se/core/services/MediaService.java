package com.vjoon.se.core.services;

import com.google.common.eventbus.EventBus;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.repository.MediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @Slf4j public class MediaService {

    @Autowired private MediaRepository repository;

    @Autowired private EventBus eventBus;

    public List<Media> getAll() {
        return repository.findAll();
    }
}
