package com.iremayvaz.content.controller;

import com.iremayvaz.content.model.dto.ChapterListItemDto;
import com.iremayvaz.content.model.dto.StoryReadInfoDto;
import com.iremayvaz.content.model.dto.request.CreateStoryRequest;
import com.iremayvaz.content.model.dto.response.StoryInfoResponseDto;
import com.iremayvaz.content.model.dto.response.StoryResponse;
import com.iremayvaz.content.model.dto.response.SuggestionResponseDto;
import com.iremayvaz.content.service.StoryCommandService;
import com.iremayvaz.content.service.StoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Story API", description = "Hikaye işlemleri")
@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class RestStoryController {

    private final StoryCommandService storyCommandService;
    private final StoryQueryService storyQueryService;

    @Operation(description = "Chapter’ı inceleme sürecine sokup Moderation’a iş oluşturur")
    @PostMapping("/create")
    public ResponseEntity<StoryResponse> createStory(@RequestParam Long authorId,
                                                     @RequestBody @Valid CreateStoryRequest createStoryRequest) {
        StoryResponse storyResponse = storyCommandService.createStory(authorId, createStoryRequest);
        return ResponseEntity.ok(storyResponse);
    }

    @Operation(description = "Bir yazarın tüm hikayeleri")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<StoryResponse>> getStoriesByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(storyQueryService.getStoriesByAuthor(authorId));
    }

    @Operation(description = "Hikayeleri Keşfet (Tüm hikayeler görüntülensin)")
    @GetMapping()
    public List<StoryResponse> getAllStories() {
        return storyQueryService.getAllStories();
    }

    @Operation(description = "Kitap veya yazar ara...")
    @GetMapping("/search/suggestions")
    public SuggestionResponseDto suggestions(@RequestParam String query,
                                             @RequestParam(defaultValue = "8") int limit) {
        limit = Math.min(Math.max(limit, 1), 20); // 1..20
        return storyQueryService.suggestions(query, limit);
    }

    @Operation(description = "Herhangi bir story'nin okunmadan önceki bilgisi")
    @GetMapping("/{storyId}/info")
    public StoryInfoResponseDto getStoryInfo(@PathVariable Long storyId,
                                             @RequestParam(required = false) Long userId) {
        return storyQueryService.getStoryInfo(storyId, userId);
    }

    @Operation(description = "HEMEN OKU BUTONU")
    @GetMapping("/{slug}/read-info")
    public StoryReadInfoDto readInfo(@PathVariable String slug) {
        return storyQueryService.getStoryReadInfo(slug);
    }

    @Operation(description = "Hikaye chapter bilgileri")
    @GetMapping("/{slug}/chapters/read")
    public List<ChapterListItemDto> readChapters(@PathVariable String slug) {
        return storyQueryService.getChaptersForRead(slug);
    }
}
