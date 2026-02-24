package com.iremayvaz.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.iremayvaz.content.model.entity.UserLibrary;

import java.util.Optional;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long> {
    boolean existsByUserIdAndStoryId(Long userId, Long storyId);
    Optional<UserLibrary> findByUserIdAndStoryId(Long userId, Long storyId);
}
