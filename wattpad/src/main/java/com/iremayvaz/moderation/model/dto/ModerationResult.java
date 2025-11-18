package com.iremayvaz.moderation.model.dto;

import com.iremayvaz.moderation.model.enums.Severity;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModerationResult {

    // ML'nin verdiği etiketler
    private List<LabelPrediction> predictions;

    // Final ceza puanı
    private Double score;

    // ML modeli hakkında debug/metadata (opsiyonel)
    private Severity severity;
}

