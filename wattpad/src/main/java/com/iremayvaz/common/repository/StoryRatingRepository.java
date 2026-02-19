package com.iremayvaz.common.repository;

import com.iremayvaz.common.model.entity.StoryRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface StoryRatingRepository extends JpaRepository<StoryRating, Long> {
    Optional<StoryRating> findByStoryIdAndUserId(Long storyId, Long userId);

    long countByStoryId(Long storyId);

    @Query("select coalesce(avg(r.value), 0) from StoryRating r where r.story.id = :storyId")
    Optional<BigDecimal> avgByStoryId(@Param("storyId") Long storyId);
}

