package com.iremayvaz.content.model.entity;

import com.iremayvaz.common.model.entity.BaseEntity;
import com.iremayvaz.content.model.enums.ChapterStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// content/model/Chapter.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "chapters",
        uniqueConstraints = @UniqueConstraint(columnNames = {"story_id", "number"}))
public class Chapter extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(nullable = false)
    private Integer number; // 1, 2, 3...

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_version_id")
    private ChapterVersion currentVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterStatus status = ChapterStatus.DRAFT;
    // DRAFT, PENDING_REVIEW, REJECTED, PUBLISHED, BLOCKED

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChapterVersion> versions = new ArrayList<>();

    @Column(name = "published_at")
    private Instant publishedAt;

    // İstersen currentVersionId tutan bir field açabilirsin
}

