package com.iremayvaz.content.service;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.model.dto.response.RatingSummaryDto;
import com.iremayvaz.common.model.entity.Comment;
import com.iremayvaz.common.model.entity.StoryRating;
import com.iremayvaz.common.model.mapper.CommentMapper;
import com.iremayvaz.common.repository.CommentRepository;
import com.iremayvaz.common.repository.FollowRepository;
import com.iremayvaz.common.repository.StoryRatingRepository;
import com.iremayvaz.content.model.dto.ChapterListItemDto;
import com.iremayvaz.content.model.dto.StoryReadInfoDto;
import com.iremayvaz.content.model.enums.StoryStatus;
import com.iremayvaz.content.model.mapper.ChapterMapper;
import com.iremayvaz.content.model.mapper.StoryMapper;
import com.iremayvaz.content.model.dto.request.SuggestionItemDto;
import com.iremayvaz.content.model.dto.response.*;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.StoryRepository;
import com.iremayvaz.content.repository.UserLibraryRepository;
import com.iremayvaz.content.security.ContentAccessPolicy;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StoryQueryService { // HEMEN OKU
    private final StoryRepository storyRepository;
    private final CommentRepository commentRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterQueryService chapterQueryService;
    private final StoryRatingRepository storyRatingRepository;
    private final UserLibraryRepository userLibraryRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    private final StoryMapper storyMapper;
    private final CommentMapper commentMapper;
    private final ContentAccessPolicy contentAccessPolicy;
    private final ChapterMapper chapterMapper;

    public List<StoryResponse> getStoriesByAuthor(Long authorId) {
        return storyRepository.findByAuthorId(authorId)
                .stream()
                .map(storyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoryInfoResponseDto getStoryInfo(Long storyId, Long userId) {

        Story s = storyRepository.findByIdWithAuthor(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found: " + storyId));

        StoryInfoResponseDto dto = storyMapper.toInfoResponse(s);

        dto.setCommentCount(commentRepository.countByStoryIdAndDeletedFalse(s.getId()));
        BigDecimal avgRating = storyRatingRepository.avgByStoryId(storyId).orElse(BigDecimal.ZERO);
        dto.setRating(new RatingSummaryDto(avgRating, storyRatingRepository.countByStoryId(storyId)));

        if (userId != null) {
            dto.setInMyList(userLibraryRepository.existsByUserIdAndStoryId(userId, s.getId()));
            dto.setMyRating(storyRatingRepository.findByStoryIdAndUserId(storyId, userId)
                    .map(StoryRating::getValue)
                    .orElse(null));

            // Takip durumunu AuthorDto içine yerleştirelim
            if (dto.getAuthor() != null) {
                boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(userId, s.getAuthor().getId());
                dto.getAuthor().setIsFollowing(isFollowing);
            }
        }

        if (dto.getAuthor() != null) {
            dto.getAuthor().setFollowerCount(followRepository.countByFollowingId(s.getAuthor().getId()));
        }

        dto.setChapters(chapterQueryService.getChaptersByOrder(s.getId(), userId, false));
        dto.setChapterCount(dto.getChapters().size());

        List<Comment> rootComments = commentRepository.findStoryGeneralComments(storyId);
        dto.setComments(rootComments.stream()
                .map(commentMapper::toDto)
                .toList());

        return dto;
    }


    public List<StoryResponse> getAllStories() {
        return storyRepository.findAll()
                .stream()
                .map(storyMapper::toResponse)
                .toList();
    }

    public SuggestionResponseDto suggestions(String query, int limit) {
        String q = normalize(query);
        if (q.length() < 2) {
            return new SuggestionResponseDto(query, List.of());
        }

        // önce DB’den “aday” çek (limit*6 gibi bir sayı iyi olur)
        int candidateSize = Math.max(30, limit * 6);
        List<Story> candidates = storyRepository.findCandidates(q, PageRequest.of(0, candidateSize));

        List<SuggestionItemDto> scored = candidates.stream()
                .map(story -> score(story, q))
                .filter(dto -> dto.getScore() > 0)
                .sorted(Comparator
                        .comparingInt(SuggestionItemDto::getScore).reversed()
                        .thenComparing(dto -> dto.getTitle() == null ? 9999 : dto.getTitle().length())
                        .thenComparing(SuggestionItemDto::getTitle, Comparator.nullsLast(String::compareToIgnoreCase))
                )
                .limit(limit)
                .toList();

        return new SuggestionResponseDto(query, scored);
    }

    @Transactional(readOnly = true)
    public StoryReadInfoDto getStoryReadInfo(String slug, Long userId) throws AccessDeniedException {
        Story story = findStoryBySlug(slug, userId);
        return storyMapper.toReadInfoDto(story);
    }

    @Transactional(readOnly = true)
    public List<ChapterListItemDto> getChaptersForRead(String slug, Long userId) {
        Story story = findStoryBySlug(slug, userId);
        List<Chapter> chapters = chapterRepository.findAllByStorySlugOrderByNumber(slug);

        return chapters.stream()
                .filter(c -> c.getStatus() == ChapterStatus.PUBLISHED)
                .map(chapterMapper::toListItemDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StoryResponse> getTrendingStories(int limit) {
        return storyRepository.findTopStoriesByCommentCount(PageRequest.of(0, limit))
                .stream()
                .map(storyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StoryResponse> getNewArrivals(int limit) {
        return storyRepository.findRecentlyUpdatedStories(PageRequest.of(0, limit))
                .stream()
                .map(storyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<StoryResponse> getAllStoriesPaged(int page, int size) {
        return storyRepository.findAllPublishedRandom(PageRequest.of(page, size))
                .map(storyMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<StoryResponse> getUserPublishedStories(Long userId) {
        return storyRepository.findByAuthorIdAndStatus(userId, StoryStatus.PUBLISHED)
                .stream()
                .map(storyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StoryResponse> getUserDraftStories(Long userId) {
        return storyRepository.findByAuthorIdAndStatus(userId, StoryStatus.DRAFT)
                .stream()
                .map(storyMapper::toResponse)
                .toList();
    }

    // PRIVATE UTILITY METHODS
    private SuggestionItemDto score(Story b, String q) {
        String title = safeLower(normalize(b.getTitle()));
        String author = safeLower(normalize(b.getAuthor().getDisplayName()));

        int score = 0;
        List<String> match = new ArrayList<>();

        if (title.startsWith(q)) { score += 100; match.add("title_prefix"); }
        if (author.startsWith(q)) { score += 90; match.add("author_prefix"); }

        if (score == 0) {
            if (title.contains(q)) { score += 40; match.add("title_contains"); }
            if (author.contains(q)) { score += 35; match.add("author_contains"); }
        }

        SuggestionItemDto dto = storyMapper.toSuggestionItemDto(b);
        dto.setScore(score);
        dto.setMatch(match);

        return dto;
    }

    private String normalize(String s) {
        if (s == null) return "";
        s = s.trim().toLowerCase(Locale.forLanguageTag("tr"));
        // TR sadeleştirme:
        s = s.replace('ı','i').replace('İ','i')
                .replace('ş','s').replace('ğ','g')
                .replace('ü','u').replace('ö','o').replace('ç','c');
        return s;
    }

    private String safeLower(String s) { return s == null ? "" : s.toLowerCase(); }

    private Story findStoryBySlug(String slug, Long userId) {
        Story story = storyRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Story not found: " + slug));

        User currentUser = (userId != null) ? userRepository.findById(userId).orElse(null) : null;

        if (!contentAccessPolicy.canReadStory(currentUser, story)) {
            throw new AccessDeniedException("Bu hikayeye erişim yetkiniz yok.");
        }

        return story;
    }
}
