package io.nutritionapp.backend.controller;

import io.nutritionapp.backend.dto.tag.TagResponse;
import io.nutritionapp.backend.model.entity.Tag;
import io.nutritionapp.backend.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@Slf4j
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        log.info("Запрос на получение всех тегов");
        List<Tag> tags = tagService.findAll();
        List<TagResponse> tagResponses = tags.stream()
                .map(this::toTagResponse)
                .toList();
        return ResponseEntity.ok(tagResponses);
    }

    private TagResponse toTagResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }
}
