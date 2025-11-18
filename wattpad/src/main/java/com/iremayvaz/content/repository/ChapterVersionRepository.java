package com.iremayvaz.content.repository;

import com.iremayvaz.content.model.entity.ChapterVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterVersionRepository extends JpaRepository<ChapterVersion, Long> {
}
