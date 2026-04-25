package com.iremayvaz.content.model.dto;

import com.iremayvaz.content.model.dto.response.StoryResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LibraryItemDto {
    private StoryResponse story;
    private String shelfStatus; // "READING", "FINISHED", "SAVED", "ALL"
}
