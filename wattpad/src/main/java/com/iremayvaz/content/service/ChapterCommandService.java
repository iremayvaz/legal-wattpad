package com.iremayvaz.content.service;

import com.iremayvaz.common.exception.ChapterAlreadyPendingException;
import com.iremayvaz.content.model.dto.response.ChapterResponse;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChapterCommandService { // YAZ/DEĞİŞTİR

    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;


    // Story'ye yeni chapter ekleniyor
    @Transactional
    public ChapterResponse createChapterDraft(Long storyId, Integer number, String title) {
        Story story = storyRepository.findById(storyId).orElseThrow();

        Chapter chapter = new Chapter();
        chapter.setStory(story);
        chapter.setNumber(number);
        chapter.setTitle(title);
        chapter.setStatus(ChapterStatus.DRAFT);

        chapterRepository.save(chapter);

        return toChapterResponse(chapter); // 0 km taslak
    }

    @Transactional
    public void submitForReview(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();

        // Hiç content/version yoksa submit edemesin
        if (chapter.getCurrentVersion() == null) {
            throw new IllegalStateException("Chapter has no content to review");
        }

        // Zaten incelemedeyse tekrar submit etmesin
        if (chapter.getStatus() == ChapterStatus.PENDING_REVIEW) {
            throw new ChapterAlreadyPendingException();
        }

        chapter.setStatus(ChapterStatus.PENDING_REVIEW);
        chapterRepository.save(chapter);
/*
        // Moderation queue item oluştur (duplicate engelle)
        boolean alreadyQueued = moderationQueueRepository
                .existsByTargetTypeAndTargetIdAndStateIn(
                        TargetType.CHAPTER,
                        chapterId,
                        List.of(QueueState.QUEUED, QueueState.AUTO_SCANNED)
                );

        if (!alreadyQueued) {
            ModerationQueueItem item = new ModerationQueueItem();
            item.setTargetType(TargetType.CHAPTER);
            item.setTargetId(chapterId);
            item.setState(QueueState.QUEUED);

            // hangi versiyon submit edildi? (çok önemli)
            if (chapter.getCurrentVersion() != null) {
                item.setChapterVersionId(chapter.getCurrentVersion().getId());
            }

            moderationQueueRepository.save(item);
        }*/
    }

    private ChapterResponse toChapterResponse(Chapter chapter) {
        ChapterResponse r = new ChapterResponse();
        r.setId(chapter.getId());
        r.setStoryId(chapter.getStory().getId());
        r.setNumber(chapter.getNumber());
        r.setTitle(chapter.getTitle());
        r.setStatus(chapter.getStatus());

        Integer latest = (chapter.getCurrentVersion() != null)
                ? chapter.getCurrentVersion().getVersionNo()
                : null;
        r.setLatestVersionNo(latest);

        return r;
    }

}
