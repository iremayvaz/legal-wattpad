package com.iremayvaz.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;          // spoiler ise null dönebilirsin
    private boolean isSpoiler;
    private boolean deleted;

    // Yorumu yapan kişi bilgileri
    private long authorId;
    private String authorUsername;
    private String authorDisplayName;
    private int likeCount;
    private int dislikeCount;
    private int replyCount;
    private Long parentId;
    private Instant createdAt;

    // YORUMUN ALTINDAKİ YANITLAR
    private List<CommentDto> replies;
}
