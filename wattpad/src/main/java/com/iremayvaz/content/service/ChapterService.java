package com.iremayvaz.content.service;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.exception.ChapterAlreadyPendingException;
import com.iremayvaz.content.model.dto.request.AddChapterVersionRequest;
import com.iremayvaz.content.model.dto.response.ChapterResponse;
import com.iremayvaz.content.model.dto.response.ChapterVersionResponse;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.ChapterVersion;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.ChapterVersionRepository;
import com.iremayvaz.content.repository.StoryRepository;
import com.iremayvaz.moderation.repository.ModerationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;
    private final ChapterVersionRepository chapterVersionRepository;
    private final UserRepository userRepository;
    private final ModerationQueueRepository moderationQueueRepository;


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
    public ChapterVersionResponse addNewVersion(Long chapterId, Long userId, AddChapterVersionRequest addChapterVersionRequest) {
        User user = userRepository.findById(userId).orElseThrow();
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();

        int nextVersionNo = chapterVersionRepository
                .findFirstByChapterIdOrderByVersionNoDesc(chapterId)
                .map(v -> v.getVersionNo() + 1)
                .orElse(1);

        ChapterVersion v = new ChapterVersion();
        v.setChapter(chapter);
        v.setVersionNo(nextVersionNo);
        v.setContent(addChapterVersionRequest.getContent());
        v.setCreatedBy(user);

        chapterVersionRepository.save(v);

        chapter.setCurrentVersion(v);
        chapterRepository.save(chapter);

        return toChapterVersionResponse(v);
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

    private ChapterVersionResponse toChapterVersionResponse(ChapterVersion v) {
        ChapterVersionResponse r = new ChapterVersionResponse();
        r.setId(v.getId());
        r.setChapterId(v.getChapter().getId());
        r.setVersionNo(v.getVersionNo());
        r.setCreatedByUserId(v.getCreatedBy() != null ? v.getCreatedBy().getId() : null);
        r.setCreatedAt(v.getCreatedAt()); // BaseEntity’de varsa
        return r;
    }
}
