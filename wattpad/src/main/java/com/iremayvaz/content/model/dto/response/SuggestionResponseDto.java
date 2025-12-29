package com.iremayvaz.content.model.dto.response;

import com.iremayvaz.content.model.dto.request.SuggestionItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SuggestionResponseDto {
    String query;
    List<SuggestionItemDto> results;
}
