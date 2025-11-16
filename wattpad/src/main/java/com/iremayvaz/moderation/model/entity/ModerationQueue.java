package com.iremayvaz.moderation.model.entity;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.common.model.entity.BaseEntity;
import com.iremayvaz.content.model.entity.ChapterVersion;
import com.iremayvaz.moderation.model.enums.ModerationState;
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

// ML servisine / moderatöre gidecek "İNCELENMEMİŞ CHAPTER"ları tutuyor.
// Satır bazında: 1 queue kaydı = 1 chapterVersion
// Tablo bazında: Queue’da farklı chapter’lardan bir sürü item aynı anda durur
@Entity
@Table(name = "moderation_queue",
        uniqueConstraints = @UniqueConstraint(columnNames = "chapter_version_id"))
public class ModerationQueue extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_version_id", nullable = false)
    private ChapterVersion chapterVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModerationState state = ModerationState.QUEUED;

    @Column(nullable = false)
    private Integer priority = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locked_by")
    private User lockedBy;

    private Instant lockedAt;

}

