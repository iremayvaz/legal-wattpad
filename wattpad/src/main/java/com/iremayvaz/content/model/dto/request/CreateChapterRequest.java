package com.iremayvaz.content.model.dto.request;

import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.ChapterStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateChapterRequest {
    @NotNull
    @Min(1)
    private Integer number;         // chapter sıra no (1,2,3...)

    @NotBlank
    @Max(200)                       // title uzunluğu kontrolü
    private String title;

    @NotBlank
    private String content;
}

