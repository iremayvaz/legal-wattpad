package com.iremayvaz.common.service;

import com.iremayvaz.auth.repository.UserRepository;
import com.iremayvaz.common.model.entity.Follow;
import com.iremayvaz.common.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) throw new IllegalArgumentException("Kendinizi takip edemezsiniz.");

        Optional<Follow> existing = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);

        if (existing.isPresent()) {
            followRepository.delete(existing.get());
        } else {
            Follow follow = new Follow();
            follow.setFollower(userRepository.getReferenceById(followerId));
            follow.setFollowing(userRepository.getReferenceById(followingId));
            followRepository.save(follow);
        }
    }
}
