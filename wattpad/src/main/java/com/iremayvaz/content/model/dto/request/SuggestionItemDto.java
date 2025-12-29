package com.iremayvaz.content.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SuggestionItemDto {
    private Long id;
    private String title;
    private String author;
    private String coverUrl;
    int score;
    List<String> match;
}
