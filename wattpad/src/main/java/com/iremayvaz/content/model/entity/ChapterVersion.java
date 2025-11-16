package com.iremayvaz.content.model.entity;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

// content/model/ChapterVersion.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "chapter_versions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chapter_id", "version_no"}))
public class ChapterVersion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo; // 1, 2, 3...

    @Lob
    @Column(nullable = false)
    private String content; // 1000+ kelimelik metnin asıl saklandığı yer

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}

