package com.iremayvaz.common.repository;

import com.iremayvaz.common.model.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    long countByFollowingId(Long followingId); // Yazarın kaç takipçisi var?
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId); // Ben bu yazarı takip ediyor muyum?
}
