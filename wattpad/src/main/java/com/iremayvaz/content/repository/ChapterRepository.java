package com.iremayvaz.content.repository;

import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.enums.ChapterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByStoryIdOrderByNumberAsc(Long storyId);
    Optional<Chapter> findByStoryIdAndNumber(Long storyId, Integer number);

    // Story sayfası: chapter listesi
    @Query("""
        select c from Chapter c
        where c.story.slug = :slug
        order by c.number asc
    """)
    List<Chapter> findAllByStorySlugOrderByNumber(@Param("slug") String slug);

    // Okuma: chapter + story + currentVersion fetch
    @Query("""
        select c from Chapter c
        join fetch c.story s
        left join fetch c.currentVersion cv
        where c.id = :id
    """)
    Optional<Chapter> findByIdWithStoryAndCurrentVersion(@Param("id") Long id);

    // Normal kullanıcıya sadece published chapter listesi istersen (opsiyonel)
    @Query("""
        select c from Chapter c
        where c.story.slug = :slug and c.status = :status
        order by c.number asc
    """)
    List<Chapter> findByStorySlugAndStatus(@Param("slug") String slug, @Param("status") ChapterStatus status);
}
