package com.iremayvaz.common.model.entity;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.content.model.entity.Story;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "story_ratings",
        uniqueConstraints = @UniqueConstraint(name="uk_story_user", columnNames = {"story_id", "user_id"}),
        indexes = {
                @Index(name="idx_story_ratings_story", columnList = "story_id"),
                @Index(name="idx_story_ratings_user", columnList = "user_id")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoryRating extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="story_id", nullable=false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(nullable=false)
    private int value; // 1..5
}
