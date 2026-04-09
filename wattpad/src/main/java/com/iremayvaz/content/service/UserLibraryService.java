package com.iremayvaz.content.service;

import com.iremayvaz.content.model.dto.mapper.StoryMapper;
import com.iremayvaz.content.model.dto.response.StoryResponse;
import com.iremayvaz.content.repository.UserLibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLibraryService {
    private final UserLibraryRepository userLibraryRepository;
    private final StoryMapper storyMapper;

    @Transactional(readOnly = true)
    public List<StoryResponse> getUserLibrary(Long userId) {
        return userLibraryRepository.findByUserId(userId).stream()
                .map(libraryEntry -> storyMapper.toResponse(libraryEntry.getStory()))
                .toList();
    }
}
