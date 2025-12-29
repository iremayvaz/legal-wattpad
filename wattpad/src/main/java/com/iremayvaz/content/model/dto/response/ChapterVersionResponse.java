package com.iremayvaz.content.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChapterVersionResponse {
    private Long id;
    private Long chapterId;
    private Integer versionNo;
    private Long createdByUserId;
    private Instant createdAt;
}
