package com.iremayvaz.content.controller;

import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.StoryRepository;
import com.iremayvaz.content.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class RestContentController {
    private final ContentService contentService;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;

    @PostMapping("/add/chapter/to/story/{story_id}")
    public ResponseEntity<Void> addChapter(@PathVariable Long story_id) {
        contentService.addChapter(story_id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create/story")
    public ResponseEntity<Void> createStory(@RequestBody @Valid Story newStory) { // DTO STORY OLARAK PARAMETRE AL!!
        contentService.createStory(newStory);
        return ResponseEntity.ok().build();
    }

}
