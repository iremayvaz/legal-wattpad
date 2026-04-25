package com.iremayvaz.content.controller;

import com.iremayvaz.content.model.dto.response.StoryResponse;
import com.iremayvaz.content.service.UserLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library")
public class LibraryController {

    private final UserLibraryService userLibraryService;

    @Operation(description = "Kullanıcının kütüphanesindeki kitapları listele")
    @GetMapping
    public ResponseEntity<List<StoryResponse>> getUserLibrary(@RequestParam Long userId) {
        return ResponseEntity.ok(userLibraryService.getUserLibrary(userId));
    }
}
