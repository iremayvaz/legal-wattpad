package com.iremayvaz.content.model.dto.response;

import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.ChapterStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChapterResponse {
    private Long id;
    private Long storyId;
    private Integer number;
    private String title;
    private ChapterStatus status;
    private Integer latestVersionNo;
}
