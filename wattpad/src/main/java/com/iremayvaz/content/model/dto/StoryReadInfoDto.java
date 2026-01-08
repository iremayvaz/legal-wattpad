package com.iremayvaz.content.model.dto;

import com.iremayvaz.content.model.enums.StoryStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StoryReadInfoDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String coverUrl;
    private StoryStatus status;

    private Long authorId;
    private String authorName;
}
