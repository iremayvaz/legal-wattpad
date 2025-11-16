package com.iremayvaz.content.model.entity;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.common.model.entity.BaseEntity;
import com.iremayvaz.content.model.enums.StoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// content/model/Story.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "stories")
public class Story extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoryStatus status = StoryStatus.DRAFT; // DRAFT, PUBLISHED, ARCHIVED, BLOCKED

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();

    // timestamps, getters, setters...
}

