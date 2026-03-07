package com.iremayvaz.content.repository;

import com.iremayvaz.content.model.entity.UserChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

public interface UserChapterProgressRepository extends JpaRepository<UserChapterProgress, Long> {
    // Kullanıcının bir hikayedeki okuduğu bölüm ID'lerini getirir
    List<UserChapterProgress> findByUserIdAndChapterStoryId(Long userId, Long storyId);
    // Kullanıcı bölümü hiç okumuş mu?
    Boolean existsByUserIdAndChapterId(Long userId, Long chapterId);
}
