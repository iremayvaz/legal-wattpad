package com.iremayvaz.common.controller;

import com.iremayvaz.common.model.dto.response.RatingSummaryDto;
import com.iremayvaz.common.model.dto.request.RateStoryRequest;
import com.iremayvaz.common.service.StoryRatingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rate")
@RequiredArgsConstructor
public class StoryRatingController {

    private final StoryRatingService storyRatingService;

    // POST /rate/story/{storyId}  body: { "value": 4 }
    @Operation(description = "Kitabı puanla")
    @PutMapping("/story/{storyId}")
    public ResponseEntity<RatingSummaryDto> rate(@PathVariable Long storyId,
                                                 @RequestBody RateStoryRequest req) {
        return ResponseEntity.ok(storyRatingService.rateStory(storyId, req.getUser_id(), req.getValue())); // Security gelince body’den kaldırıp current user’dan çekeceksin
    }
}
