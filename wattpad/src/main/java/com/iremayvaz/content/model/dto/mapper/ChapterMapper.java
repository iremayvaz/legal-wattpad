package com.iremayvaz.content.model.dto.mapper;

import com.iremayvaz.content.model.dto.ChapterListItemDto;
import com.iremayvaz.content.model.dto.ChapterReadDto;
import com.iremayvaz.content.model.dto.ChapterSummaryDto;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.enums.ChapterStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {ChapterStatus.class})
public interface ChapterMapper {

    // Chapter -> ChapterSummaryDto
    @Mapping(target = "published", expression = "java(chapter.getStatus() == ChapterStatus.PUBLISHED)")
    @Mapping(target = "read", ignore = true) // Kullanıcıya özel bilgi, serviste setlenir
    ChapterSummaryDto toSummaryDto(Chapter chapter);

    // Chapter -> ChapterReadDto (Okuma sayfası)
    @Mapping(target = "storyId", source = "story.id")
    @Mapping(target = "storySlug", source = "story.slug")
    @Mapping(target = "currentVersionNo", source = "currentVersion.versionNo")
    @Mapping(target = "content", source = "currentVersion.content")
    @Mapping(target = "comments", ignore = true)
    ChapterReadDto toReadDto(Chapter chapter);

    // Chapter -> ChapterListItemDto
    @Mapping(target = "readable", constant = "true")
    ChapterListItemDto toListItemDto(Chapter chapter);
}
