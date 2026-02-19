package com.iremayvaz.content.model.dto;

import com.iremayvaz.content.model.enums.ChapterStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChapterSummaryDto {
    private Long id;
    private int number;
    private String title;
    private ChapterStatus status;
    private boolean published;
    private Instant publishedAt;
    private boolean read;
}
