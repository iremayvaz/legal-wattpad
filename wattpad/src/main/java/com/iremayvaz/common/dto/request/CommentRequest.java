package com.iremayvaz.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private Long userId;
    private String content;

    @JsonProperty("isSpoiler")
    private boolean isSpoiler;
    private Long parentId; // Yanıt (reply) ise dolu gelecek
}
