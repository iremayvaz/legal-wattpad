package com.iremayvaz.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;          // spoiler ise null dönebilirsin
    private boolean spoiler;
    private boolean deleted;
    private long authorId;
    private String authorUsername;
    private String authorDisplayName;
    private int likeCount;
    private int dislikeCount;
    private int replyCount;
    private Long parentId;
    private Instant createdAt;
}
