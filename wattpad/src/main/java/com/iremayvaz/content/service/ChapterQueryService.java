package com.iremayvaz.content.service;

import com.iremayvaz.content.model.dto.ChapterReadDto;
import com.iremayvaz.content.model.dto.ChapterSummaryDto;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.ChapterVersion;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.model.enums.StoryStatus;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.StoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterQueryService { // HEMEN OKU

    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;

    @Transactional(readOnly = true)
    public ChapterReadDto getChapterRead(Long chapterId) {
        Chapter chapter = chapterRepository.findByIdWithStoryAndCurrentVersion(chapterId)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found: " + chapterId));

        // Public okuma: story + chapter published olmalı
        if (chapter.getStory().getStatus() != StoryStatus.PUBLISHED ||
                chapter.getStatus() != ChapterStatus.PUBLISHED) {
            throw new EntityNotFoundException("Chapter not found: " + chapterId);
        }

        ChapterVersion cv = chapter.getCurrentVersion();

        // PUBLISHED chapter için currentVersion null olmamalı
        if (cv == null) {
            throw new IllegalStateException("Published chapter has no currentVersion: " + chapterId);
        }

        return ChapterReadDto.builder()
                .id(chapter.getId())
                .storyId(chapter.getStory().getId())
                .storySlug(chapter.getStory().getSlug())
                .number(chapter.getNumber())
                .title(chapter.getTitle())
                .status(chapter.getStatus())
                .currentVersionNo(cv.getVersionNo())
                .content(cv.getContent())
                .build();
    }

    public List<ChapterSummaryDto> getChaptersByOrder(Long story_id, boolean chaptersNewestFirst) {
        Story s = storyRepository.findByIdWithAuthor(story_id)
                .orElseThrow(() -> new EntityNotFoundException("Story not found: " + story_id));

        List<Chapter> chapterEntities = chaptersNewestFirst
                ? chapterRepository.findByStoryIdOrderByNumberDesc(s.getId())
                : chapterRepository.findByStoryIdOrderByNumberAsc(s.getId());

        List<ChapterSummaryDto> chapters = chapterEntities.stream()
                .map(ch -> new ChapterSummaryDto(
                        ch.getId(),
                        ch.getNumber(),
                        ch.getTitle(),
                        ch.getStatus(),
                        ch.getStatus() == ChapterStatus.PUBLISHED, // <-- burada
                        ch.getPublishedAt(),
                        false // not: user-specific değilse şimdilik böyle
                ))
                .toList();


        return chapters;
    }
}
