package com.iremayvaz.content.service;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.repository.CommentRepository;
import com.iremayvaz.content.model.dto.request.CreateStoryRequest;
import com.iremayvaz.content.model.dto.response.StoryResponse;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.entity.UserLibrary;
import com.iremayvaz.content.model.enums.StoryStatus;
import com.iremayvaz.content.repository.StoryRepository;
import com.iremayvaz.content.repository.UserLibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoryCommandService { // YAZ/DEĞİŞTİR

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final UserLibraryRepository userLibraryRepository;
    private final CommentRepository commentRepository;

    // Yeni hikaye oluşturuluyor
    public StoryResponse createStory(Long authorId, CreateStoryRequest createStoryRequest) {
        User author = userRepository.findById(authorId) // // Şimdilik authorId dışarıdan geliyor varsayalım (Auth gelince token’dan alacağız)
                .orElseThrow();
        author.setId(authorId);

        Story story = new Story();
        story.setAuthor(author);
        story.setTitle(createStoryRequest.getTitle());
        story.setDescription(createStoryRequest.getDescription());
        story.setStatus(StoryStatus.DRAFT);

        story.setSlug(generateSlug(createStoryRequest.getTitle()));

        Story saved = storyRepository.save(story);
        return toStoryResponse(story);
    }

    @Transactional
    public void toggleLibrary(Long storyId, Long userId) {
        Optional<UserLibrary> existing = userLibraryRepository.findByUserIdAndStoryId(userId, storyId);
        if (existing.isPresent()) {
            userLibraryRepository.delete(existing.get());
        } else {
            UserLibrary ul = new UserLibrary();
            ul.setUser(userRepository.getReferenceById(userId));
            ul.setStory(storyRepository.getReferenceById(storyId));
            userLibraryRepository.save(ul);
        }
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9ğüşöçıİ ]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

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
}
