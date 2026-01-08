package com.iremayvaz.content.model.dto;

import com.iremayvaz.content.model.enums.ChapterStatus;
import lombok.*;

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
}

