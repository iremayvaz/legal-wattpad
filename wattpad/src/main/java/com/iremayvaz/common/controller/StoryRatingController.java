package com.iremayvaz.common.controller;

import com.iremayvaz.common.dto.response.RatingSummaryDto;
import com.iremayvaz.common.dto.request.RateStoryRequest;
import com.iremayvaz.common.service.StoryRatingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryRatingController {

    private final StoryRatingService storyRatingService;

    // POST /api/stories/{storyId}/rating   body: { "value": 4 }
    @Operation(description = "Kitabı puanla")
    @PutMapping("/{story_id}/rate")
    public ResponseEntity<RatingSummaryDto> rate(
            @PathVariable Long story_id,
            @RequestBody RateStoryRequest req
    ) {
        return ResponseEntity.ok(storyRatingService.rateStory(story_id, req.getUser_id(), req.getValue())); // Security gelince body’den kaldırıp current user’dan çekeceksin
    }
}
