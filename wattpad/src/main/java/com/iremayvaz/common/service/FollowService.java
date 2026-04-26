package com.iremayvaz.common.service;

import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.model.entity.Follow;
import com.iremayvaz.common.repository.FollowRepository;
import com.iremayvaz.content.model.dto.response.AuthorDto;
import com.iremayvaz.content.model.mapper.StoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final StoryMapper storyMapper;

    @Transactional
    public String toggleFollow(Long followerId, Long followingId) {
        String message;
        if (followerId.equals(followingId)) throw new IllegalArgumentException("Kendinizi takip edemezsiniz.");

        Optional<Follow> existing = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);

        if (existing.isPresent()) {
            followRepository.delete(existing.get());
            message = "Takipten çıkıldı.";
        } else {
            Follow follow = new Follow();
            follow.setFollower(userRepository.getReferenceById(followerId));
            follow.setFollowing(userRepository.getReferenceById(followingId));
            followRepository.save(follow);
            message = follow.getFollowing().getDisplayName() + " , " +
                      follow.getFollower().getDisplayName() + " tarafından takip edildi.";
        }

        return message;
    }

    // Followers
    @Transactional(readOnly = true)
    public List<AuthorDto> getFollowers(Long userId) {
        return followRepository.findByFollowingId(userId).stream()
                .map(follow -> {
                    AuthorDto dto = storyMapper.toAuthorDto(follow.getFollower());
                    dto.setFollowerCount(followRepository.countByFollowingId(follow.getFollower().getId()));
                    dto.setIsFollowing(followRepository.existsByFollowerIdAndFollowingId(userId, follow.getFollower().getId()));
                    return dto;
                })
                .toList();
    }

    // Following
    @Transactional(readOnly = true)
    public List<AuthorDto> getFollowing(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(follow -> {
                    AuthorDto dto = storyMapper.toAuthorDto(follow.getFollowing());
                    dto.setFollowerCount(followRepository.countByFollowingId(follow.getFollowing().getId()));
                    dto.setIsFollowing(true); // Zaten takip ettiğim için true
                    return dto;
                })
                .toList();
    }
}
