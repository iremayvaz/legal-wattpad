package com.iremayvaz.moderation.model.dto;

import com.iremayvaz.moderation.model.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LabelPrediction {
    private String labelCode;
    private Severity severity;
    private double score;
}