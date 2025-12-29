package com.iremayvaz.content.service;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.content.model.dto.request.CreateChapterRequest;
import com.iremayvaz.content.model.dto.request.UpdateChapterContentRequest;
import com.iremayvaz.content.model.dto.response.ChapterResponse;
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
    public ChapterResponse updateChapterContent(Long chapterId, Long authorId, UpdateChapterContentRequest req) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();

        int latest = chapterVersionRepository.findFirstByChapterIdOrderByVersionNoDesc(chapterId)
                .map(ChapterVersion::getVersionNo)
                .orElse(0);

        ChapterVersion newVersion = new ChapterVersion();
        newVersion.setChapter(chapter);
        newVersion.setVersionNo(latest + 1);
        newVersion.setContent(req.getContent());

        User user = new User();
        user.setId(authorId);
        newVersion.setCreatedBy(user);

        chapterVersionRepository.save(newVersion);

        return toChapterResponse(chapter, latest + 1);
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
}
