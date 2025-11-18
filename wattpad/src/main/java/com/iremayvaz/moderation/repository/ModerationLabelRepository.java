package com.iremayvaz.moderation.repository;

import com.iremayvaz.moderation.model.entity.ModerationLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModerationLabelRepository extends JpaRepository<ModerationLabel, Long> {
}
