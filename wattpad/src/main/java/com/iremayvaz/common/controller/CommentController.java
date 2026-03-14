package com.iremayvaz.common.controller;

import com.iremayvaz.common.dto.request.CommentRequest;
import com.iremayvaz.common.model.enums.ReactionType;
import com.iremayvaz.common.service.CommentCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentCommandService commentCommandService;

    @Operation(description = "Hikayeye genel yorum yapma")
    @PostMapping("/story/{storyId}")
    public ResponseEntity<Void> commentOnStory(@PathVariable Long storyId, @RequestBody CommentRequest req) {
        commentCommandService.postComment(storyId, null, req);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Belirli bir bölüme yorum yapma")
    @PostMapping("/story/{storyId}/chapter/{chapterId}")
    public ResponseEntity<Void> commentOnChapter(
            @PathVariable Long storyId,
            @PathVariable Long chapterId,
            @RequestBody CommentRequest req) {
        commentCommandService.postComment(storyId, chapterId, req);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Yoruma tepki verme: LIKE/DISLIKE")
    @PostMapping("/{commentId}/react")
    public ResponseEntity<Void> react(
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @RequestParam ReactionType type) {
        commentCommandService.reactToComment(commentId, userId, type);
        return ResponseEntity.ok().build();
    }
}
