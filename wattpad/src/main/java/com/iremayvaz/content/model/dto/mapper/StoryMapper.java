package com.iremayvaz.content.model.dto.mapper;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.content.model.dto.response.AuthorDto;
import com.iremayvaz.content.model.dto.response.StoryInfoResponseDto;
import com.iremayvaz.content.model.dto.response.StoryResponse;
import com.iremayvaz.content.model.entity.Story;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoryMapper {

    // Story -> StoryResponse (Genel liste için)
    @Mapping(target = "authorId", source = "author.id")
    StoryResponse toResponse(Story story);

    // Story -> StoryInfoResponseDto (Detay sayfası için)
    @Mapping(target = "chapters", ignore = true) // Servis katmanında özel işlendiği için ignore
    @Mapping(target = "comments", ignore = true) // Servis katmanında özel işlendiği için ignore
    @Mapping(target = "rating", ignore = true)       // Hesaplandığı için ignore
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "chapterCount", ignore = true)
    @Mapping(target = "inMyList", ignore = true)
    @Mapping(target = "myRating", ignore = true)
    StoryInfoResponseDto toInfoResponse(Story story);

    // User -> AuthorDto
    @Mapping(target = "displayName", expression = "java(user.getDisplayName())")
    @Mapping(target = "followerCount", constant = "0L")
    AuthorDto toAuthorDto(User user);
}
