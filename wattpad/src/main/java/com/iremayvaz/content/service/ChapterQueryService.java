package com.iremayvaz.content.service;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.model.entity.Comment;
import com.iremayvaz.common.model.mapper.CommentMapper;
import com.iremayvaz.common.repository.CommentRepository;
import com.iremayvaz.content.model.dto.ChapterReadDto;
import com.iremayvaz.content.model.dto.ChapterSummaryDto;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.ChapterVersion;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.entity.UserChapterProgress;
import com.iremayvaz.content.model.mapper.ChapterMapper;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.StoryRepository;
import com.iremayvaz.content.repository.UserChapterProgressRepository;
import com.iremayvaz.content.security.ContentAccessPolicy;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.AccessDeniedException;
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

    private final CommentMapper commentMapper;
    private final ChapterMapper chapterMapper;
    private final ContentAccessPolicy contentAccessPolicy;

    @Transactional
    public ChapterReadDto getChapterRead(Long chapterId, Long userId) throws AccessDeniedException {
        Chapter chapter = chapterRepository.findByIdWithStoryAndCurrentVersion(chapterId)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found: " + chapterId));

        User currentUser = (userId != null) ? userRepository.findById(userId).orElse(null) : null;

        // Eğer userId gelmişse, okuma geçmişine kaydet
        if (userId != null) {
            saveProgress(userId, chapter);
        }

        // Public okuma: story + chapter published olmalı
        if (!contentAccessPolicy.canReadChapter(currentUser, chapter)) { // Kullanıcı okuyabilir mi?
            throw new AccessDeniedException("Bu bölüme erişim yetkiniz yok.");
        }

        ChapterVersion cv = chapter.getCurrentVersion();

        // PUBLISHED chapter için currentVersion null olmamalı
        if (cv == null) {
            throw new IllegalStateException("Published chapter has no currentVersion: " + chapterId);
        }

        ChapterReadDto dto = chapterMapper.toReadDto(chapter);

        // Bölüm yorumları
        List<Comment> chapterComments = commentRepository.findChapterComments(chapterId);
        dto.setComments(chapterComments.stream().map(commentMapper::toDto).toList());

        return dto;
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
        return chapterEntities.stream()
                .map(ch -> chapterMapper.toSummaryDto(ch, finalReadIds.contains(ch.getId())))
                .toList();
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

}
