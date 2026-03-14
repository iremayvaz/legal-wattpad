package com.iremayvaz.content.service;

import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.dto.CommentDto;
import com.iremayvaz.common.model.entity.Comment;
import com.iremayvaz.common.repository.CommentRepository;
import com.iremayvaz.content.model.dto.ChapterReadDto;
import com.iremayvaz.content.model.dto.ChapterSummaryDto;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.ChapterVersion;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.entity.UserChapterProgress;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.model.enums.StoryStatus;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.StoryRepository;
import com.iremayvaz.content.repository.UserChapterProgressRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterQueryService { // HEMEN OKU

    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;
    private final UserChapterProgressRepository userChapterProgressRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ChapterReadDto getChapterRead(Long chapterId, Long userId) {
        Chapter chapter = chapterRepository.findByIdWithStoryAndCurrentVersion(chapterId)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found: " + chapterId));

        // Eğer userId gelmişse, okuma geçmişine kaydet
        if (userId != null) {
            saveProgress(userId, chapter);
        }

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

        // Bölüm yorumları
        List<Comment> chapterComments = commentRepository.findChapterComments(chapterId);
        List<CommentDto> comments = chapterComments.stream()
                .map(this::toCommentDto).toList();

        return ChapterReadDto.builder()
                .id(chapter.getId())
                .storyId(chapter.getStory().getId())
                .storySlug(chapter.getStory().getSlug())
                .number(chapter.getNumber())
                .title(chapter.getTitle())
                .status(chapter.getStatus())
                .currentVersionNo(cv.getVersionNo())
                .content(cv.getContent())
                .comments(comments)
                .build();
    }

    public List<ChapterSummaryDto> getChaptersByOrder(Long storyId, Long userId, boolean chaptersNewestFirst) {
        Story s = storyRepository.findByIdWithAuthor(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found: " + storyId));

        List<Chapter> chapterEntities = chaptersNewestFirst
                ? chapterRepository.findByStoryIdOrderByNumberDesc(s.getId())
                : chapterRepository.findByStoryIdOrderByNumberAsc(s.getId());

        // Kullanıcının okuduğu bölümleri set olarak al
        Set<Long> readChapterIds = Collections.emptySet(); // Set'in complexity'si daha düşük
        if (userId != null) {
            readChapterIds = userChapterProgressRepository.findByUserIdAndChapterStoryId(userId, storyId)
                    .stream().map(p -> p.getChapter().getId()).collect(Collectors.toSet());
        }

        final Set<Long> finalReadIds = readChapterIds;
        return chapterEntities.stream().map(ch -> toDto(ch, finalReadIds.contains(ch.getId()))).toList();
    }

    private void saveProgress(Long userId, Chapter chapter) {
        // Daha önce kaydedilmemişse ekle
        if (!userChapterProgressRepository.existsByUserIdAndChapterId(userId, chapter.getId())) {
            UserChapterProgress progress = new UserChapterProgress();
            progress.setUser(userRepository.getReferenceById(userId));
            progress.setChapter(chapter);
            progress.setReadAt(Instant.now());
            userChapterProgressRepository.save(progress);
        }
    }

    private ChapterSummaryDto toDto(Chapter chapter, boolean isRead) {
        return new ChapterSummaryDto(chapter.getId(), chapter.getNumber(), chapter.getTitle(), chapter.getStatus(),
                chapter.getStatus() == ChapterStatus.PUBLISHED, chapter.getPublishedAt(), isRead);
    }

    private CommentDto toCommentDto(Comment c) {
        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setContent(c.isSpoiler() ? null : c.getContent()); // Spoiler mantığı: içerik gizleme
        dto.setSpoiler(c.isSpoiler());

        // YORUM YAZARI BİLGİLERİ
        dto.setAuthorId(c.getAuthor().getId());
        dto.setAuthorUsername(c.getAuthor().getUsername());
        dto.setAuthorDisplayName(c.getAuthor().getDisplayName());

        dto.setLikeCount(c.getLikeCount());
        dto.setDislikeCount(c.getDislikeCount());
        dto.setReplyCount(c.getReplyCount());
        dto.setCreatedAt(c.getCreatedAt());

        // REPLIES
        if (c.getReplies() != null && !c.getReplies().isEmpty()) {
            dto.setReplies(c.getReplies().stream()
                    .map(this::toCommentDto)
                    .toList());
        }

        return dto;
    }
}
