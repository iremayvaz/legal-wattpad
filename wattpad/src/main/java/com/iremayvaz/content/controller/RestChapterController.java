package com.iremayvaz.content.controller;

import com.iremayvaz.content.model.dto.request.AddChapterVersionRequest;
import com.iremayvaz.content.model.dto.response.ChapterResponse;
import com.iremayvaz.content.model.dto.response.ChapterVersionResponse;
import com.iremayvaz.content.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chapter API", description = "Bölüm işlemleri")
@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor
public class RestChapterController {

    private final ChapterService chapterService;

    @PostMapping("/draft")
    public ResponseEntity<ChapterResponse> createDraft(
            @RequestParam Long storyId,
            @RequestParam Integer number,
            @RequestParam(required = false) String title
    ) {
        return ResponseEntity.ok(chapterService.createChapterDraft(storyId, number, title));
    }

    @PostMapping("/{chapterId}/versions")
    public ResponseEntity<ChapterVersionResponse> addVersion(
            @PathVariable Long chapterId,
            @RequestParam Long userId,
            @RequestBody AddChapterVersionRequest addChapterVersionRequest
    ) {
        return ResponseEntity.ok(chapterService.addNewVersion(chapterId, userId, addChapterVersionRequest));
    }

    @Operation(description = "Chapter’ı inceleme sürecine sokup Moderation’a iş oluşturur")
    @PostMapping("/{chapterId}/submit")
    public ResponseEntity<Void> submit(@PathVariable Long chapterId) {
        chapterService.submitForReview(chapterId);
        return ResponseEntity.ok().build();
    }

}
