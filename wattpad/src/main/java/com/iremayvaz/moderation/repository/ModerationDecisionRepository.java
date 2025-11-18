package com.iremayvaz.moderation.repository;

import com.iremayvaz.moderation.model.entity.ModerationDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModerationDecisionRepository extends JpaRepository<ModerationDecision, Long> {
}
