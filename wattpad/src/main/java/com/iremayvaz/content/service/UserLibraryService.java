package com.iremayvaz.content.service;

import com.iremayvaz.content.model.dto.LibraryItemDto;
import com.iremayvaz.content.model.entity.Story;
import com.iremayvaz.content.model.entity.UserLibrary;
import com.iremayvaz.content.model.enums.ChapterStatus;
import com.iremayvaz.content.model.enums.LibraryStatus;
import com.iremayvaz.content.model.mapper.StoryMapper;
import com.iremayvaz.content.model.dto.response.StoryResponse;
import com.iremayvaz.content.repository.ChapterRepository;
import com.iremayvaz.content.repository.UserChapterProgressRepository;
import com.iremayvaz.content.repository.UserLibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLibraryService {
    private final UserLibraryRepository userLibraryRepository;
    private final UserChapterProgressRepository progressRepository;
    private final ChapterRepository chapterRepository;
    private final StoryMapper storyMapper;

    @Transactional(readOnly = true)
    public List<LibraryItemDto> getUserLibrary(Long userId) {
        // Kullanıcının kütüphanesindeki tüm kayıtlar
        List<UserLibrary> libraryEntries = userLibraryRepository.findByUserId(userId);

        return libraryEntries.stream().map(entry -> {
            Story story = entry.getStory();
            LibraryItemDto dto = new LibraryItemDto();
            dto.setStory(storyMapper.toResponse(story));

            // Yayınlanmış toplam bölüm sayısı
            long totalChapters = chapterRepository.findAllByStorySlugOrderByNumber(story.getSlug()).stream()
                    .filter(c -> c.getStatus() == ChapterStatus.PUBLISHED).count();

            // Kullanıcının okuduğu bölüm sayısı
            long readChapters = progressRepository.findByUserIdAndChapterStoryId(userId, story.getId()).size();

            // Dinamik Statü Belirleme
            if (readChapters == 0) {
                dto.setShelfStatus(LibraryStatus.SAVED.toString());
            } else if (readChapters >= totalChapters && totalChapters > 0) {
                dto.setShelfStatus(LibraryStatus.FINISHED.toString());
            } else {
                dto.setShelfStatus(LibraryStatus.READING.toString());
            }

            return dto;
        }).toList();
    }
}
