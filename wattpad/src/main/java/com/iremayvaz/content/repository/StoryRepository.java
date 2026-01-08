package com.iremayvaz.content.repository;

import com.iremayvaz.content.model.entity.Story;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
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

}
