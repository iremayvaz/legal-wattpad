package com.iremayvaz.common.repository;

import com.iremayvaz.common.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // sadece root yorumlar (parent null) + en yeniler
    Page<Comment> findByStoryIdAndDeletedFalseAndParentIsNullOrderByCreatedAtDesc(Long storyId, Pageable pageable);

    @Query("SELECT c FROM Comment c JOIN FETCH c.author LEFT JOIN FETCH c.replies r LEFT JOIN FETCH r.author WHERE c.story.id = :storyId AND c.parent IS NULL")
    List<Comment> findRootCommentsWithAuthorAndReplies(@Param("storyId") Long storyId);

    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.author " +
            "LEFT JOIN FETCH c.replies r " +
            "LEFT JOIN FETCH r.author " +
            "WHERE c.story.id = :storyId " +
            "AND c.chapter IS NULL " + // Sadece hikaye genelindeki yorumlar
            "AND c.parent IS NULL")    // Sadece ana yorumlar (yanıtlar değil)
    List<Comment> findStoryGeneralComments(@Param("storyId") Long storyId);

    // Bölüm sayfası için sorgu:
    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.author " +
            "LEFT JOIN FETCH c.replies r " +
            "LEFT JOIN FETCH r.author " +
            "WHERE c.chapter.id = :chapterId " + // Sadece bu bölüme ait yorumlar
            "AND c.parent IS NULL")
    List<Comment> findChapterComments(@Param("chapterId") Long chapterId);
}
