package com.iremayvaz.content.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateStoryRequest {
    @NotBlank
    @Size(min = 1, max = 200)
    private String title;

    @Size(max = 2000)
    private String description;
}
