package com.iremayvaz.common.controller;

import com.iremayvaz.common.service.FollowService;
import com.iremayvaz.content.model.dto.response.AuthorDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @Operation(description = "Yazar/Kullanıcı takip etme")
    @PostMapping("/follow/{followingId}")
    public ResponseEntity<String> toggleFollow(
            @PathVariable Long followingId,
            @RequestParam Long followerId) {
        String message = followService.toggleFollow(followerId, followingId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<AuthorDto>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<AuthorDto>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
}
