package com.iremayvaz.content.model.dto.response;

import com.iremayvaz.common.dto.CommentDto;
import com.iremayvaz.common.dto.response.RatingSummaryDto;
import com.iremayvaz.content.model.dto.ChapterSummaryDto;
import com.iremayvaz.content.model.enums.StoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StoryInfoResponseDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String coverUrl;
    private StoryStatus status;

    private AuthorDto author;

    private RatingSummaryDto rating;     // 4.7 + “3 puanlama / 4 yorum”
    private Long commentCount;

    // UI: Bölümler
    private int chapterCount;
    private List<ChapterSummaryDto> chapters;

    // UI: Okur yorumları (ilk sayfa)
    private List<CommentDto> comments;

    // UI: kullanıcıya özel alanlar (opsiyonel)
    private Boolean inMyList; // login yoksa null dönebilirsin
    private Integer myRating;
}
