package com.iremayvaz.moderation.repository;

import com.iremayvaz.moderation.model.entity.ModerationQueue;
import com.iremayvaz.moderation.model.enums.ModerationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModerationQueueRepository extends JpaRepository<ModerationQueue, Long> {
    List<ModerationQueue> findByStateIn(List<ModerationState> states);
}
