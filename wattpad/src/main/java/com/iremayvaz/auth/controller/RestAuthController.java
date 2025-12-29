package com.iremayvaz.auth.controller;

import com.iremayvaz.auth.model.dto.AuthorInfoResponse;
import com.iremayvaz.auth.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Auth API", description = "Auth işlemleri")
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class RestAuthController {
    private final AuthService authService;
/*
    private final AuthService authService;

    @Operation(description = "Kaydolma")
    @PostMapping("/register")
    public ResponseEntity<DtoUser> register(@RequestBody @Valid DtoUserInsert dtoUserInsert) {
        var newUser = authService.register(dtoUserInsert);
        return ResponseEntity.created(URI.create("/users/" + newUser.getEmail())).body(newUser);
    }

 */

    @GetMapping("/get/author/{authorId}")
    public AuthorInfoResponse getAuthorInfo(@PathVariable("authorId") Long authorId) {
        return authService.getAuthorInfo(authorId);
    }
}
