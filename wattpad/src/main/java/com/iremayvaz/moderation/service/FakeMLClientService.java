package com.iremayvaz.moderation.service;

import com.iremayvaz.moderation.model.dto.LabelPrediction;
import com.iremayvaz.moderation.model.dto.ModerationResult;
import com.iremayvaz.moderation.model.enums.Severity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FakeMLClientService implements MLClientService {

    /*@Override
    public ModerationResult analyzeText(String text) {
        ModerationResult result = new ModerationResult();
        result.setLabelCode("SEXUAL_CONTENT");
        result.setSeverity(Severity.MEDIUM);
        result.setScore(0.87);
        return result;
    }*/

    // herhangi bir label SEVERITY : HIGH ise diğerleri LOW olsa bile SEVERITY : HIGH olarak ayarlanmalı
    // risk skor kimin büyükse o ayarlanmalı
    @Override
    public ModerationResult analyzeText(String text) {
        // 1) ML modelini çağır, tahminleri al (örnek)
        List<LabelPrediction> predictions = callYourModel(text);

        // 2) En kötü severity ve en yüksek skoru hesapla
        Severity worstSeverity = Severity.LOW;  // başlangıç
        double maxRiskScore = 0.0;

        for (LabelPrediction p : predictions) {
            // severity: LOW < MEDIUM < HIGH (enum sırası önemli)
            if (p.getSeverity().ordinal() > worstSeverity.ordinal()) {
                worstSeverity = p.getSeverity();
            }

            // score: en büyük olana bak
            if (p.getScore() != 0 && p.getScore() > maxRiskScore) {
                maxRiskScore = p.getScore();
            }
        }

        // 3) ModerationResult oluştur
        ModerationResult result = new ModerationResult();
        result.setPredictions(predictions);
        result.setSeverity(worstSeverity);   // getSeverity() burada bu olacak
        result.setScore(maxRiskScore);       // istersen adı maxRiskScore olsun
        return result;
    }


    private List<LabelPrediction> callYourModel(String text) {
        // ML hazır olana kadar uyduruk veri döndür
        List<LabelPrediction> list = new ArrayList<>();

        // örnek 1
        LabelPrediction p1 = new LabelPrediction();
        p1.setLabelCode("SEXUAL_CONTENT");
        p1.setSeverity(Severity.MEDIUM);
        p1.setScore(0.65);

        // örnek 2
        LabelPrediction p2 = new LabelPrediction();
        p2.setLabelCode("HATE_SPEECH");
        p2.setSeverity(Severity.HIGH);
        p2.setScore(0.83);

        list.add(p1);
        list.add(p2);

        return list;
    }

}
