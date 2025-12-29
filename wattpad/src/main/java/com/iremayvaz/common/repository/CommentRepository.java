package com.iremayvaz.common.repository;

import com.iremayvaz.common.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Story-level comments (chapter_id null)
    Page<Comment> findByStoryIdAndChapterIsNullAndParentIsNull(Long storyId, Pageable p);

    // Chapter comments
    Page<Comment> findByChapterIdAndParentIsNull(Long chapterId, Pageable p);

    // Replies
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    long countByStoryIdAndDeletedFalse(Long storyId);
}
