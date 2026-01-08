package com.iremayvaz.content.security;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.content.model.entity.Chapter;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.model.enums.StoryStatus;
import org.springframework.stereotype.Component;

@Component
public class ContentAccessPolicy {

    public boolean isAuthor(User currentUser, Story story) {
        if (currentUser == null || story == null || story.getAuthor() == null) return false;
        return story.getAuthor().getId().equals(currentUser.getId());
    }

    public boolean isEditor(User currentUser) {
        if (currentUser == null || currentUser.getRoles() == null) return false;
        // Rol isimlerini kendi Role entity'ne göre ayarla:
        return currentUser.getRoles().stream().anyMatch(r ->
                "EDITOR".equalsIgnoreCase(r.getRoleName().toString()) || "ADMIN".equalsIgnoreCase(r.getRoleName().toString()));
    }

    public boolean canReadStory(User currentUser, Story story) {
        if (story.getStatus() == StoryStatus.PUBLISHED) return true;

        // PUBLISHED değilse sadece author/editor
        return isAuthor(currentUser, story) || isEditor(currentUser);
    }

    public boolean canReadChapter(User currentUser, Chapter chapter) {
        Story story = chapter.getStory();
        if (!canReadStory(currentUser, story)) return false;

        if (chapter.getStatus() == ChapterStatus.PUBLISHED) return true;

        // Chapter published değilse: author/editor
        return isAuthor(currentUser, story) || isEditor(currentUser);
    }

    public boolean isReadableForNormalUser(Chapter chapter) {
        return chapter.getStatus() == ChapterStatus.PUBLISHED
                && chapter.getStory().getStatus() == StoryStatus.PUBLISHED;
    }
}

