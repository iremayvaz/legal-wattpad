package com.iremayvaz.auth.service;

import com.iremayvaz.auth.model.dto.AuthorInfoResponse;
import com.iremayvaz.auth.model.entity.User;
import com.iremayvaz.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public AuthorInfoResponse getAuthorInfo(Long authorId) {
        Optional<User> user = userRepository.findById(authorId);
        AuthorInfoResponse authorInfoResponse = new AuthorInfoResponse();
        if (user.isPresent()) {
            BeanUtils.copyProperties(user.get(), authorInfoResponse);
        }

        return authorInfoResponse;
    }
}
