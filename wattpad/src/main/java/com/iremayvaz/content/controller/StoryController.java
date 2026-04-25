package com.iremayvaz.content.controller;

import com.iremayvaz.common.service.FileService;
import com.iremayvaz.content.model.dto.ChapterListItemDto;
import com.iremayvaz.content.model.dto.StoryReadInfoDto;
import com.iremayvaz.content.model.dto.request.CreateStoryRequest;
import com.iremayvaz.content.model.dto.response.StoryInfoResponseDto;
import com.iremayvaz.content.model.dto.response.StoryResponse;
import com.iremayvaz.content.model.dto.response.SuggestionResponseDto;
import com.iremayvaz.content.model.enums.StoryStatus;
import com.iremayvaz.content.service.StoryCommandService;
import com.iremayvaz.content.service.StoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@Tag(name = "Story API", description = "Hikaye işlemleri")
@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {

    private final StoryCommandService storyCommandService;
    private final StoryQueryService storyQueryService;
    private final FileService fileService;

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
    public StoryReadInfoDto readInfo(@PathVariable String slug,
                                     @RequestParam Long userId) throws AccessDeniedException {
        return storyQueryService.getStoryReadInfo(slug,  userId);
    }

    @Operation(description = "LİSTEME EKLE BUTONU")
    @PostMapping("/{storyId}/library")
    public void addToLibrary(@PathVariable Long storyId,
                             @RequestParam Long userId) {
        storyCommandService.toggleLibrary(storyId, userId);
    }

    @Operation(description = "Hikaye chapter bilgileri")
    @GetMapping("/{slug}/chapters/read")
    public List<ChapterListItemDto> readChapters(@PathVariable String slug,
                                                 @RequestParam Long userId) {
        return storyQueryService.getChaptersForRead(slug, userId);
    }

    @Operation(description = "Hikayeye kapak fotoğrafı yükle")
    @PostMapping("/{storyId}/upload-cover")
    public ResponseEntity<String> uploadCover(@PathVariable Long storyId,
                                              @RequestParam("file") MultipartFile file) {
        String coverUrl = fileService.saveFile(file, "covers"); // Dosyayı kaydet
        storyCommandService.uploadCover(storyId,  coverUrl);
        return ResponseEntity.ok(coverUrl);
    }

    @Operation(description = "Popüler hikayeleri görüntüle")
    @GetMapping("/popular/ones")
    public List<StoryResponse> getTrendingStories(@RequestParam(defaultValue = "8") int limit) {
        int safeLimit = Math.min(limit, 50); // TEK SAYFADA MAX GORUNTULENEBİLİRLİK
        return storyQueryService.getTrendingStories(safeLimit);
    }

    @Operation(description = "Yeni çıkan hikayeleri görüntüle")
    @GetMapping("/new/ones")
    public List<StoryResponse> getNewArrivals(@RequestParam(defaultValue = "8") int limit) {
        int safeLimit = Math.min(limit, 50);
        return storyQueryService.getNewArrivals(safeLimit);
    }

    @Operation(description = "Keşfet sayfası")
    @GetMapping("/fyp")
    public Page<StoryResponse> getAllStoriesPaged(@RequestParam(defaultValue = "8") int page,
                                                  @RequestParam(defaultValue = "8") int size) {
        return storyQueryService.getAllStoriesPaged(page, size);
    }

    @Operation(description = "Kullanıcının yazdıkları")
    @GetMapping("/that/i/have")
    public List<StoryResponse> getUserPublishedStories(@RequestParam Long userId,
                                                       @RequestParam StoryStatus status) {
        if (status == StoryStatus.PUBLISHED) {
            return storyQueryService.getUserPublishedStories(userId);   // YAYINLANANLAR
        } else {
            return storyQueryService.getUserDraftStories(userId);       // TASLAKLAR
        }
    }
}
