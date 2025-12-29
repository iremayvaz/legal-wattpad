package com.iremayvaz.common.model.entity;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.common.model.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "comment_reactions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_comment_user_reaction",
                columnNames = {"comment_id", "user_id"}
        ),
        indexes = {
                @Index(name = "idx_comment_reactions_comment", columnList = "comment_id"),
                @Index(name = "idx_comment_reactions_user", columnList = "user_id")
        })
public class CommentReaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ReactionType type; // LIKE, DISLIKE
}
