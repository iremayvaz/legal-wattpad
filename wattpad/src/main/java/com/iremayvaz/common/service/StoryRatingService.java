package com.iremayvaz.common.service;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.model.entity.StoryRating;
import com.iremayvaz.common.repository.StoryRatingRepository;
import com.iremayvaz.common.dto.response.RatingSummaryDto;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.repository.StoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StoryRatingService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryRatingRepository storyRatingRepository;

    @Transactional
    public RatingSummaryDto rateStory(Long storyId, Long userId, int value) {
        if (value < 0 || value > 5) throw new IllegalArgumentException("0..5 olmalı");

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found: " + storyId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        StoryRating r = storyRatingRepository.findByStoryIdAndUserId(storyId, userId)
                .orElseGet(() -> {
                    StoryRating x = new StoryRating();
                    x.setStory(story);
                    x.setUser(user);
                    return x;
                });

        r.setValue(value);
        storyRatingRepository.save(r);

        BigDecimal avg = storyRatingRepository.avgByStoryId(storyId).orElse(BigDecimal.ZERO);
        long count = storyRatingRepository.countByStoryId(storyId);

        return new RatingSummaryDto(avg, count);
    }

    public int findByStoryIdAndUserId(Long storyId, Long currentUser_id) {
        return storyRatingRepository.findByStoryIdAndUserId(storyId, currentUser_id)
                .map(StoryRating::getValue).orElseThrow(() -> new EntityNotFoundException("Story not found: " + storyId));
    }
}

