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
public class StoryInfoResponseDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String coverUrl;
    private StoryStatus status;

    private AuthorDto author;

    private RatingSummaryDto rating;     // 4.7 + “3 puanlama / 4 yorum”
    private Long commentCount;

    private Boolean inMyList; // login yoksa null dönebilirsin
}
