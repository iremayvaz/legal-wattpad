package com.iremayvaz.common.controller;

import com.iremayvaz.common.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @Operation(description = "Yazar/Kullanıcı takip etme")
    @PostMapping("/follow/{followingId}")
    public ResponseEntity<Void> toggleFollow(
            @PathVariable Long followingId,
            @RequestParam Long followerId) {
        followService.toggleFollow(followerId, followingId);
        return ResponseEntity.ok().build();
    }
}
