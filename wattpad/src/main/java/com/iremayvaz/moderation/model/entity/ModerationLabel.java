package com.iremayvaz.moderation.model.entity;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.common.model.entity.BaseEntity;
import com.iremayvaz.moderation.model.enums.LabelSource;
import com.iremayvaz.moderation.model.enums.LabelType;
import com.iremayvaz.moderation.model.enums.Severity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// ML METNİ İNCELEDİKTEN SONRA chapter için etiketler
@Entity
@Table(name = "moderation_labels")
public class ModerationLabel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", nullable = false)
    private ModerationQueue queue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LabelSource source;

    @Column(name = "label_code", nullable = false, length = 64)
    private String labelCode; // "SEXUAL_CONTENT", "MINOR_INVOLVEMENT" vs.

    @Enumerated(EnumType.STRING)
    private LabelType mappedType; // Bilinen etiketler, nullable olabilir

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Severity severity;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    @Column(name = "score", precision = 5, scale = 4, nullable = true)
    private BigDecimal score;

    @Column(name = "evidence_spans", columnDefinition = "text")
    private String evidenceSpansJson;
    // ML bu etiketi neden verdi? (karar sebepleri)

    @Column(columnDefinition = "text")
    private String notes;
    // her etiket sadece ML tarafından verilmeyecek

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy; // ML ise NULL, HUMAN ise {user_name}
}

