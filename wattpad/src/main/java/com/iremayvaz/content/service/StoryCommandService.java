package com.iremayvaz.content.service;

import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.content.model.dto.request.CreateStoryRequest;
import com.iremayvaz.content.model.dto.response.StoryResponse;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.entity.UserLibrary;
import com.iremayvaz.content.model.enums.StoryStatus;
import com.iremayvaz.content.model.mapper.StoryMapper;
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

    private final StoryMapper storyMapper;

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
        storyRepository.save(story);

        return storyMapper.toResponse(story);
    }

    // Kullanıcı kütüphanesine kayıt ediliyor
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

    // Hikayeye kapak fotoğrafı yükleniyor
    public void uploadCover(Long storyId, String coverUrl) {
        Story story = storyRepository.findById(storyId).orElseThrow();

        // Veritabanını güncelle
        story.setCoverUrl(coverUrl);
        storyRepository.save(story);
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
