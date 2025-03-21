package io.nutritionApp.service;

import io.nutritionApp.model.entity.Tag;
import io.nutritionApp.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TagService {
    private static final Logger log = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> findAll() {
        log.debug("Получение всех тегов");
        return tagRepository.findAll();
    }

    public Optional<Tag> findById(UUID id) {
        log.debug("Поиск тега: {}", id);
        return tagRepository.findById(id);
    }

    public Optional<Tag> findByName(String name) {
        log.debug("Поиск тега: {}", name);
        return tagRepository.findByName(name);
    }
}
