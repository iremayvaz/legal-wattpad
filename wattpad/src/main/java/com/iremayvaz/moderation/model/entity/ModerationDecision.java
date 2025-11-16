package com.iremayvaz.moderation.model.entity;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.common.model.entity.BaseEntity;
import com.iremayvaz.moderation.model.enums.ModerationDecisionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// Bu chapter için FİNAL kararı
@Entity
@Table(name = "moderation_decisions")
public class ModerationDecision extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", nullable = false, unique = true)
    private ModerationQueue queue;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_status", nullable = false, length = 20)
    private ModerationDecisionStatus finalStatus;

    @Column(name = "penalty_points")
    private Integer penaltyPoints;

    @Column(name = "decision_summary", columnDefinition = "text")
    private String decisionSummary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    private User decidedBy;

    private Instant decidedAt = Instant.now();
}

