package com.iremayvaz.moderation.controller;

import com.iremayvaz.moderation.model.entity.ModerationQueue;
import com.iremayvaz.moderation.model.enums.ModerationState;
import com.iremayvaz.moderation.repository.ModerationQueueRepository;
import com.iremayvaz.moderation.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderation")
@RequiredArgsConstructor
public class RestModerationController {
    private final ModerationService moderationService;
    private final ModerationQueueRepository moderationQueueRepository;

    /**
     * Şu id’li moderation kuyruğu elemanını çalıştır, ML ile incele ve kararını ver.
     *
     * Tek bir queue item’ını işler (ML + karar).
     * Örn: POST /api/moderation/queue/5/process
     * Birden fazla chapter olduğunda
     * processAll metodu yaz
     * arka tarafta döngüyle hepsini işle
     */
    @PostMapping("/queue/{queueId}/process")
    public ResponseEntity<Void> processQueueItem(@PathVariable Long queueId) {
        moderationService.processQueueItem(queueId);
        return ResponseEntity.ok().build();
    }

    /**
     * şu anda incelenmeyi bekleyen bütün kuyruğu getir
     *
     * İnsan moderatör veya test için
     * state = QUEUED / AUTO_SCANNED olanları listele.
     * Örn: GET /moderation/queue/pending
     */
    @GetMapping("/queue/pending")
    public ResponseEntity<List<ModerationQueue>> getPendingQueueItems() {
        List<ModerationQueue> items =
                moderationQueueRepository.findByStateIn(
                        List.of(ModerationState.QUEUED, ModerationState.AUTO_SCANNED)
                );
        return ResponseEntity.ok(items);
    }
}
