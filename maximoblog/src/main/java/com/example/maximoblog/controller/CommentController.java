package com.example.maximoblog.controller;

import com.example.maximoblog.dto.CommentRequest;
import com.example.maximoblog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> addComment(
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        return commentService.addComment(request, authentication.getName());
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getCommentsByArticle(
            @PathVariable("articleId") Long articleId) {
        return commentService.getCommentsByArticle(articleId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("id") Long id,
            Authentication authentication) {
        return commentService.deleteComment(id, authentication.getName());
    }
}
