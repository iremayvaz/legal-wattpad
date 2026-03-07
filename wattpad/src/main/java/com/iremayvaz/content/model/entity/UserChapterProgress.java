package com.iremayvaz.content.model.entity;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "user_chapter_progress",
        // Aynı user + aynı chapter ikilisinden sadece 1 tane olabilir
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_chapter_progress",
                columnNames = {"user_id", "chapter_id"}
        ))
@Getter @Setter
public class UserChapterProgress extends BaseEntity { // User - Chapter arasındaki ara tablo (Many-to-Many)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    private Instant readAt; // ilk okunduğu an? son okunduğu an?
}
