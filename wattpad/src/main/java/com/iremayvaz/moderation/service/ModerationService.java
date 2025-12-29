package com.iremayvaz.moderation.service;

import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.ChapterVersion;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.moderation.model.dto.LabelPrediction;
import com.iremayvaz.moderation.model.dto.ModerationResult;
import com.iremayvaz.moderation.model.entity.ModerationDecision;
import com.iremayvaz.moderation.model.entity.ModerationLabel;
import com.iremayvaz.moderation.model.entity.ModerationQueue;
import com.iremayvaz.moderation.model.enums.LabelSource;
import com.iremayvaz.moderation.model.enums.ModerationDecisionStatus;
import com.iremayvaz.moderation.model.enums.ModerationState;
import com.iremayvaz.moderation.model.enums.Severity;
import com.iremayvaz.moderation.repository.ModerationDecisionRepository;
import com.iremayvaz.moderation.repository.ModerationLabelRepository;
import com.iremayvaz.moderation.repository.ModerationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@RequiredArgsConstructor
@Service
public class ModerationService { // Moderation servisi ML’i kullanır!
    // MlClientService ile ML’i çağırır
    // ML analizi sonrası çıkan kararı sisteme KESİN KARAR olarak kaydeder.

    private final MLClientService mlClientService;
    private final ModerationLabelRepository moderationLabelRepository;
    private final ModerationQueueRepository moderationQueueRepository;
    private final ModerationDecisionRepository moderationDecisionRepository;
    private final ChapterRepository chapterRepository;

    @Transactional
    public void processQueueItem(Long queueId) {
        ModerationQueue queue = moderationQueueRepository.findById(queueId)
                .orElseThrow();

        ChapterVersion chapterVersion = queue.getChapterVersion();
        Chapter chapter = chapterVersion.getChapter();
        String text = chapterVersion.getContent();  // chapter'ın tam metni

        // ML Servisinden sonucu al
        ModerationResult result = mlClientService.analyzeText(text);

        // Modelin tüm label tahminlerini moderationLabel tablosuna kaydet
        for (LabelPrediction p : result.getPredictions()) {
            ModerationLabel label = new ModerationLabel();
            label.setQueue(queue);
            label.setSource(LabelSource.ML);
            label.setLabelCode(p.getLabelCode());
            label.setSeverity(p.getSeverity());
            label.setScore(p.getScore());
            moderationLabelRepository.save(label);
        }

        // severity ve score'a göre "OTOMATİK KARAR"
        ModerationDecision decision = new ModerationDecision();
        decision.setQueue(queue);

        if (result.getSeverity() == Severity.HIGH) {
            // yüksek risk → BLOCK
            decision.setFinalStatus(ModerationDecisionStatus.BLOCK);
            decision.setDecisionSummary("ML: yüksek risk, otomatik bloklandı.");
            chapter.setStatus(ChapterStatus.BLOCKED);
            queue.setState(ModerationState.DECIDED);
        } else if (result.getSeverity() == Severity.LOW
                && result.getScore() != null
                && result.getScore().compareTo(new BigDecimal("0.3")) < 0) {
            // düşük risk → APPROVE
            decision.setFinalStatus(ModerationDecisionStatus.APPROVE);
            decision.setDecisionSummary("ML: düşük risk, otomatik onaylandı.");
            chapter.setCurrentVersion(chapterVersion); // YAYINLANACAK VERS KAYDEDİLİR
            chapter.setStatus(ChapterStatus.PUBLISHED);
            chapter.setPublishedAt(Instant.now());
            queue.setState(ModerationState.DECIDED);
        } else {
            // gri alan → HUMAN_REVIEW
            queue.setState(ModerationState.HUMAN_REVIEW);
            // decision yaratmayabilir veya status = EDIT_REQUIRED/HOLD gibi bırakabilirsin
            moderationQueueRepository.save(queue);
            return;
        }

        // Karar ne zaman verildi?
        decision.setDecidedAt(Instant.now());

        // Kararı ve güncellenen durumları kaydet
        moderationDecisionRepository.save(decision);
        chapterRepository.save(chapter);
        moderationQueueRepository.save(queue);
    }
}
