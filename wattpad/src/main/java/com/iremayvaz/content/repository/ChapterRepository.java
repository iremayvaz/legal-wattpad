package com.iremayvaz.content.repository;

import com.iremayvaz.content.model.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByStoryIdOrderByNumberAsc(Long storyId);
    Optional<Chapter> findByStoryIdAndNumber(Long storyId, Integer number);

}
