package com.iremayvaz.common.model.mapper;

import com.iremayvaz.common.model.dto.CommentDto;
import com.iremayvaz.common.model.dto.request.CommentRequest;
import com.iremayvaz.common.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "authorDisplayName", expression = "java(comment.getAuthor().getDisplayName())")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "content", expression = "java(comment.isSpoiler() ? null : comment.getContent())")
    CommentDto toDto(Comment comment);

    // CommentMapper.java
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true) // Serviste id ile çekilecek
    @Mapping(target = "story", ignore = true)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Comment toEntity(CommentRequest req);

}
