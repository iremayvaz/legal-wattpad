package com.iremayvaz.content.model.dto;

import com.iremayvaz.common.model.dto.CommentDto;
import com.iremayvaz.content.model.enums.ChapterStatus;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChapterReadDto {
    private Long id;
    private Long storyId;
    private String storySlug;

    private Integer number;
    private String title;
    private ChapterStatus status;

    private Integer currentVersionNo;
    private String content;

    private List<CommentDto> comments;
}

