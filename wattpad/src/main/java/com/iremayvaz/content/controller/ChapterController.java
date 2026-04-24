package com.iremayvaz.content.controller;

import com.iremayvaz.content.model.dto.ChapterReadDto;
import com.iremayvaz.content.model.dto.ChapterSummaryDto;
import com.iremayvaz.content.model.dto.request.AddChapterVersionRequest;
import com.iremayvaz.content.model.dto.response.ChapterResponse;
import com.iremayvaz.content.model.dto.response.ChapterVersionResponse;
import com.iremayvaz.content.service.ChapterCommandService;
import com.iremayvaz.content.service.ChapterQueryService;
import com.iremayvaz.content.service.ChapterVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@Tag(name = "Chapter API", description = "Bölüm işlemleri")
@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterCommandService chapterCommandService;
    private final ChapterQueryService chapterQueryService;
    private final ChapterVersionService chapterVersionService;

    @Operation(description = "Hikaye bölümü taslak")
    @PostMapping("/draft")
    public ResponseEntity<ChapterResponse> createDraft(@RequestParam Long storyId,
                                                       @RequestParam Integer number,
                                                       @RequestParam(required = false) String title) {
        return ResponseEntity.ok(chapterCommandService.createChapterDraft(storyId, number, title));
    }

    @Operation(description = "Bölüm versiyonu ekle (kullanıcı SUBMIT der)")
    @PostMapping("/{chapterId}/versions")
    public ResponseEntity<ChapterVersionResponse> addVersion(@PathVariable Long chapterId,
                                                             @RequestParam Long userId,
                                                             @RequestBody AddChapterVersionRequest addChapterVersionRequest) {
        return ResponseEntity.ok(chapterVersionService.addNewVersion(chapterId, userId, addChapterVersionRequest));
    }

    @Operation(description = "Chapter’ı inceleme sürecine gönderip Moderation’a iş oluşturur")
    @PostMapping("/{chapterId}/submit")
    public ResponseEntity<Void> submit(@PathVariable Long chapterId) {
        chapterCommandService.submitForReview(chapterId);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Bölümü oku")
    @GetMapping("/{chapterId}/read") // Story id alınmalı sanki?
    public ChapterReadDto readChapter(@PathVariable Long chapterId,
                                      @RequestParam Long userId) throws AccessDeniedException {
        return chapterQueryService.getChapterRead(chapterId, userId);
    }

    @Operation(description = "Bölümleri sırayla al (Bölümleri görüntülerken)")
    @GetMapping("/story/{storyId}")
    public List<ChapterSummaryDto> getChaptersByOrder(@PathVariable Long storyId,
                                                      @RequestParam Long userId,
                                                      @RequestParam(defaultValue = "false") boolean chaptersNewestFirst) {
        return chapterQueryService.getChaptersByOrder(storyId, userId, chaptersNewestFirst);
    }

}
