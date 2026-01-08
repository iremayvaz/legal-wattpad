package com.iremayvaz.content.model.dto;

import com.iremayvaz.content.model.enums.ChapterStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChapterListItemDto {
    private Long id;
    private Integer number;
    private String title;
    private ChapterStatus status;
    private boolean readable; // kullanıcı bu chapter'ı okuyabiliyor mu?
}

