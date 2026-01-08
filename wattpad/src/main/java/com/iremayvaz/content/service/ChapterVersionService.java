package com.iremayvaz.content.service;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.content.model.dto.request.AddChapterVersionRequest;
import com.iremayvaz.content.model.dto.request.CreateChapterRequest;
import com.iremayvaz.content.model.dto.response.ChapterResponse;
import com.iremayvaz.content.model.dto.response.ChapterVersionResponse;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.ChapterVersion;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.ChapterVersionRepository;
import com.iremayvaz.content.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChapterVersionService {

    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterVersionRepository chapterVersionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChapterResponse createChapter(Long storyId, Long authorId, CreateChapterRequest createChapterRequest) {
        Story story = storyRepository.findById(storyId).orElseThrow();

        Chapter chapter = new Chapter();
        chapter.setStory(story);
        chapter.setNumber(createChapterRequest.getNumber());
        chapter.setTitle(createChapterRequest.getTitle());
        chapter.setStatus(ChapterStatus.DRAFT);

        Chapter savedChapter = chapterRepository.save(chapter);

        ChapterVersion v1 = new ChapterVersion();
        v1.setChapter(savedChapter);
        v1.setVersionNo(1);
        v1.setContent(createChapterRequest.getContent());

        User user = new User();
        user.setId(authorId);
        v1.setCreatedBy(user);

        chapterVersionRepository.save(v1);

        return toChapterResponse(chapter, 1);
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

    private ChapterResponse toChapterResponse(Chapter chapter, Integer latestVersionNo) {
        ChapterResponse chapterResponse = new ChapterResponse();
        chapterResponse.setId(chapter.getId());
        chapterResponse.setStoryId(chapter.getStory().getId());
        chapterResponse.setNumber(chapter.getNumber());
        chapterResponse.setTitle(chapter.getTitle());
        chapterResponse.setStatus(chapter.getStatus());
        chapterResponse.setLatestVersionNo(latestVersionNo);
        return chapterResponse;
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
