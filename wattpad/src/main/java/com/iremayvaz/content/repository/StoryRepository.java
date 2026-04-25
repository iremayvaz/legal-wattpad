package com.iremayvaz.content.repository;

import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.StoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByAuthorId(Long authorId);
    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = {"author"}) // authorName için
    Optional<Story> findBySlug(String slug);


    @Query("""
    select s
    from Story s
    join s.author a
    where lower(s.title) like lower(concat('%', :q, '%'))
       or lower(a.username) like lower(concat('%', :q, '%'))
       or lower(concat(coalesce(a.firstName,''), ' ', coalesce(a.lastName,'')))
            like lower(concat('%', :q, '%'))
    """)
    List<Story> findCandidates(@Param("q") String q, Pageable pageable);

    @Query("""
        select s
        from Story s
        join fetch s.author a
        where s.id = :id
    """)
    Optional<Story> findByIdWithAuthor(@Param("id") Long id);

    @Query("SELECT s FROM Story s LEFT JOIN Comment c ON s.id = c.story.id " +
            "WHERE s.status = 'PUBLISHED' " +
            "GROUP BY s.id ORDER BY COUNT(c.id) DESC")
    List<Story> findTopStoriesByCommentCount(Pageable pageable);

    @Query("SELECT DISTINCT s FROM Story s JOIN s.chapters c " +
            "WHERE s.status = 'PUBLISHED' AND c.status = 'PUBLISHED' " +
            "ORDER BY c.publishedAt DESC")
    List<Story> findRecentlyUpdatedStories(Pageable pageable);

    // StoryRepository.java
    @Query("SELECT s FROM Story s WHERE s.status = 'PUBLISHED' ORDER BY function('RAND')")
    Page<Story> findAllPublishedRandom(Pageable pageable);

    List<Story> findByAuthorIdAndStatus(Long userId, StoryStatus storyStatus);
}
