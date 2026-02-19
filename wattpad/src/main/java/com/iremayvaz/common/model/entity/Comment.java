package com.iremayvaz.common.model.entity;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.Story;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comments_story", columnList = "story_id"),
                @Index(name = "idx_comments_chapter", columnList = "chapter_id"),
                @Index(name = "idx_comments_parent", columnList = "parent_id"),
                @Index(name = "idx_comments_author", columnList = "author_id"),
                @Index(name = "idx_comments_created", columnList = "created_at")
        })
public class Comment extends BaseEntity { // yorumun kendisi (content, spoiler, parent/replies vb.)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // HER YORUM MUTLAKA STORY'YE AİT
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    // STORY yorumu: null, CHAPTER yorumu: dolu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isSpoiler = false;

    // thread (reply)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Comment> replies = new ArrayList<>();

    // denormalize alanlar
    @Column(nullable = false)
    private int replyCount = 0;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int dislikeCount = 0;

    @Column(nullable = false)
    private boolean deleted = false;

    public boolean isChapterComment() {
        return chapter != null;
    }

    public void addReply(Comment reply) {
        if (reply == null) return;

        reply.setParent(this);

        // reply aynı bağlamda kalmalı
        reply.setStory(this.story);
        reply.setChapter(this.chapter);

        this.replies.add(reply);
        this.replyCount++;
    }
}
