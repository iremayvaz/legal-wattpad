package com.iremayvaz.content.service;

import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.dto.CommentDto;
import com.iremayvaz.common.dto.response.RatingSummaryDto;
import com.iremayvaz.common.model.entity.Comment;
import com.iremayvaz.common.model.entity.StoryRating;
import com.iremayvaz.common.repository.CommentRepository;
import com.iremayvaz.common.repository.StoryRatingRepository;
import com.iremayvaz.common.service.StoryRatingService;
import com.iremayvaz.content.model.dto.ChapterListItemDto;
import com.iremayvaz.content.model.dto.ChapterSummaryDto;
import com.iremayvaz.content.model.dto.StoryReadInfoDto;
import com.iremayvaz.content.model.dto.request.SuggestionItemDto;
import com.iremayvaz.content.model.dto.response.*;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.model.enums.StoryStatus;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.StoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    public List<StoryResponse> getStoriesByAuthor(Long authorId) {
        return storyRepository.findByAuthorId(authorId)
                .stream()
                .map(this::toStoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoryInfoResponseDto getStoryInfo(Long storyId, Long userId) {

        Story s = storyRepository.findByIdWithAuthor(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found: " + storyId));

        long commentCount = commentRepository.countByStoryIdAndDeletedFalse(s.getId());

        // Rating sistemi henüz yoksa:
        BigDecimal avgRating = storyRatingRepository.avgByStoryId(storyId)
                .orElse(BigDecimal.ZERO); // null dönebilir
        long ratingCount = storyRatingRepository.countByStoryId(storyId);

        RatingSummaryDto rating = new RatingSummaryDto(avgRating, ratingCount);

        Integer myRating = null;
        if (userId != null) {
            myRating = storyRatingRepository.findByStoryIdAndUserId(storyId, userId)
                    .map(StoryRating::getValue)
                    .orElse(null);
        }

        // Listeme ekle / benim puanım (şimdilik yoksa null)
        Boolean inMyList = null;

        AuthorDto authorDto = new AuthorDto(
                s.getAuthor().getId(),
                s.getAuthor().getUsername(),
                s.getAuthor().getDisplayName()
        );

        // --------- CHAPTERS (UI: Bölümler) ----------
        List<ChapterSummaryDto> chapters = chapterQueryService.getChaptersByOrder(s.getId(),
                                                                false); // EN ESKİDEN EN YENİYE
        int chapterCount = chapters.size();

        // --------- COMMENTS (UI: Okur yorumları) ----------
        // İlk sayfada örnek 10 root yorum göster
        Page<Comment> rootComments = commentRepository
                .findByStoryIdAndDeletedFalseAndParentIsNullOrderByCreatedAtDesc(
                        s.getId(),
                        PageRequest.of(0, 10)
                );

        List<CommentDto> comments = rootComments.getContent().stream()
                .map(c -> new CommentDto(
                        c.getId(),
                        (c.isSpoiler() ? null : c.getContent()),
                        c.isSpoiler(),
                        c.isDeleted(),
                        c.getAuthor().getId(),
                        c.getAuthor().getUsername(),
                        c.getAuthor().getDisplayName(),
                        c.getLikeCount(),
                        c.getDislikeCount(),
                        c.getReplyCount(),
                        (c.getParent() == null ? null : c.getParent().getId()),
                        c.getCreatedAt()
                ))
                .toList();

        return new StoryInfoResponseDto(
                s.getId(),
                s.getTitle(),       // Başlık
                s.getSlug(),        // Hikaye adının slug'ı
                s.getDescription(), // Hikaye hakkında
                s.getCoverUrl(),    // Kapak fotoğrafı
                s.getStatus(),      // Story durumu
                authorDto,          // Yazar Bilgisi
                rating,             // Oy
                commentCount,       // Yorum
                chapterCount,       // Bölüm sayısı
                chapters,           // Bölümler
                comments,           // Yorumlar
                inMyList,           // Listeme ekle
                myRating            // Oyum
        );
    }


    public List<StoryResponse> getAllStories() {
        return storyRepository.findAll()
                .stream()
                .map(this::toStoryResponse)
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
                .map(b -> score(b, q))
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
    public StoryReadInfoDto getStoryReadInfo(String slug) {
        Story story = storyRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Story not found: " + slug));

        // Public okuma: sadece PUBLISHED story
        if (story.getStatus() != StoryStatus.PUBLISHED) {
            // 404 gibi davranmak daha güvenli
            throw new EntityNotFoundException("Story not found: " + slug);
        }

        return StoryReadInfoDto.builder()
                .id(story.getId())
                .title(story.getTitle())
                .slug(story.getSlug())
                .description(story.getDescription())
                .coverUrl(story.getCoverUrl())
                .status(story.getStatus())
                .authorId(story.getAuthor().getId())
                .authorName(story.getAuthor().getDisplayName())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChapterListItemDto> getChaptersForRead(String slug) {
        Story story = storyRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Story not found: " + slug));

        if (story.getStatus() != StoryStatus.PUBLISHED) {
            throw new EntityNotFoundException("Story not found: " + slug);
        }

        List<Chapter> chapters = chapterRepository.findAllByStorySlugOrderByNumber(slug);

        return chapters.stream()
                .filter(c -> c.getStatus() == ChapterStatus.PUBLISHED)
                .map(c -> ChapterListItemDto.builder()
                        .id(c.getId())
                        .number(c.getNumber())
                        .title(c.getTitle())
                        .status(c.getStatus())
                        .readable(true)
                        .build())
                .toList();
    }

    // PRIVATE UTILITY METHODS
    private SuggestionItemDto score(Story b, String q) {
        String title = safeLower(normalize(b.getTitle()));
        String author = safeLower(normalize(b.getAuthor().getFirstName() + ' ' + b.getAuthor().getLastName()));

        int score = 0;
        List<String> match = new ArrayList<>();

        if (title.startsWith(q)) { score += 100; match.add("title_prefix"); }
        if (author.startsWith(q)) { score += 90; match.add("author_prefix"); }

        if (score == 0) {
            if (title.contains(q)) { score += 40; match.add("title_contains"); }
            if (author.contains(q)) { score += 35; match.add("author_contains"); }
        }

        return new SuggestionItemDto(
                b.getId(),
                b.getTitle(),
                (b.getAuthor().getFirstName() + ' ' + b.getAuthor().getLastName()),
                b.getCoverUrl(),
                score,
                match
        );
    }

    private String normalize(String s) {
        if (s == null) return "";
        s = s.trim().toLowerCase(Locale.forLanguageTag("tr"));
        // İstersen TR sadeleştirme:
        s = s.replace('ı','i').replace('İ','i')
                .replace('ş','s').replace('ğ','g')
                .replace('ü','u').replace('ö','o').replace('ç','c');
        return s;
    }

    private String safeLower(String s) { return s == null ? "" : s.toLowerCase(); }

    private StoryResponse toStoryResponse(Story story) {
        StoryResponse storyResponse = new StoryResponse();
        storyResponse.setId(story.getId());
        storyResponse.setAuthorId(story.getAuthor().getId());
        storyResponse.setTitle(story.getTitle());
        storyResponse.setSlug(story.getSlug());
        storyResponse.setDescription(story.getDescription());
        storyResponse.setStatus(story.getStatus());
        return storyResponse;
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9ğüşöçıİ ]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    private String generateUniqueSlug(String title) {
        String base = generateSlug(title);
        String slug = base;
        int i = 2;

        while (storyRepository.existsBySlug(slug)) {
            slug = base + "-" + i;
            i++;
        }
        return slug;
    }
}
