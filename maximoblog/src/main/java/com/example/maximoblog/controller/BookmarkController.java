package com.example.maximoblog.controller;

import com.example.maximoblog.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{articleId}")
    public ResponseEntity<?> addBookmark(
            @PathVariable("articleId") Long articleId,
            Authentication authentication) {
        return bookmarkService.addBookmark(articleId, authentication.getName());
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> removeBookmark(
            @PathVariable("articleId") Long articleId,
            Authentication authentication) {
        return bookmarkService.removeBookmark(articleId, authentication.getName());
    }

    @GetMapping
    public ResponseEntity<?> getUserBookmarks(Authentication authentication) {
        return bookmarkService.getUserBookmarks(authentication.getName());
    }
}
