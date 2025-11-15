package com.iremayvaz.content.model.entity;

import com.iremayvaz.common.model.ChapterStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// content/model/Chapter.java
@Entity
@Table(name = "chapters",
        uniqueConstraints = @UniqueConstraint(columnNames = {"story_id", "number"}))
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(nullable = false)
    private Integer number; // 1, 2, 3...

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterStatus status = ChapterStatus.DRAFT;
    // DRAFT, PENDING_REVIEW, REJECTED, PUBLISHED, BLOCKED

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChapterVersion> versions = new ArrayList<>();

    // İstersen currentVersionId tutan bir field açabilirsin
}

