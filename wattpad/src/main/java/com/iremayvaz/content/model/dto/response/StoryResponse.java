package com.iremayvaz.content.model.dto.response;

import com.iremayvaz.content.model.enums.StoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StoryResponse {
    private Long id;
    private Long authorId;
    //private String authorName;
    //private String coverPhoto;
    private String title;
    private String slug;
    private String description;
    private StoryStatus status; // DRAFT, PUBLISHED, ARCHIVED, BLOCKED
}
