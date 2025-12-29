package com.iremayvaz.content.repository;

import com.iremayvaz.content.model.entity.ChapterVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterVersionRepository extends JpaRepository<ChapterVersion, Long> {
    List<ChapterVersion> findByChapterIdOrderByVersionNoDesc(Long chapterId);
    Optional<ChapterVersion> findFirstByChapterIdOrderByVersionNoDesc(Long chapterId);

}
