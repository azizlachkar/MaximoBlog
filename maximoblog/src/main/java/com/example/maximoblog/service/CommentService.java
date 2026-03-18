package com.example.maximoblog.service;

import com.example.maximoblog.dto.CommentRequest;
import com.example.maximoblog.dto.CommentResponse;
import com.example.maximoblog.entity.Article;
import com.example.maximoblog.entity.Comment;
import com.example.maximoblog.entity.Role;
import com.example.maximoblog.entity.User;
import com.example.maximoblog.repository.ArticleRepository;
import com.example.maximoblog.repository.CommentRepository;
import com.example.maximoblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    private static final int MAX_REPLY_DEPTH = 3;

    // ── Add Comment ─────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> addComment(CommentRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        Article article = articleRepository.findById(request.getArticleId()).orElse(null);
        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Article not found"));
        }

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId()).orElse(null);
            if (parentComment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Parent comment not found"));
            }

            // Check reply depth
            if (getDepth(parentComment) >= MAX_REPLY_DEPTH) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Maximum reply depth of " + MAX_REPLY_DEPTH + " reached"));
            }

            // Ensure reply is on the same article
            if (!parentComment.getArticle().getId().equals(article.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Parent comment does not belong to this article"));
            }
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .article(article)
                .parentComment(parentComment)
                .build();

        Comment saved = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved, 0));
    }

    // ── Get Comments for Article ────────────────────────────────

    @Transactional(readOnly = true)
    public ResponseEntity<?> getCommentsByArticle(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Article not found"));
        }

        List<Comment> topLevelComments = commentRepository
                .findByArticleIdAndParentCommentIsNullOrderByCreatedAtDesc(articleId);

        List<CommentResponse> responses = new ArrayList<>();
        for (Comment comment : topLevelComments) {
            responses.add(toResponse(comment, 0));
        }

        return ResponseEntity.ok(Map.of(
                "articleId", articleId,
                "totalComments", commentRepository.countByArticleId(articleId),
                "comments", responses
        ));
    }

    // ── Delete Comment ──────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Comment not found"));
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        // Only the comment owner or an ADMIN can delete
        boolean isOwner = comment.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only delete your own comments"));
        }

        commentRepository.delete(comment); // cascades to replies
        return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
    }

    // ── Helpers ─────────────────────────────────────────────────

    private int getDepth(Comment comment) {
        int depth = 0;
        Comment current = comment;
        while (current.getParentComment() != null) {
            depth++;
            current = current.getParentComment();
        }
        return depth;
    }

    private CommentResponse toResponse(Comment comment, int depth) {
        List<CommentResponse> replyResponses;
        if (depth < MAX_REPLY_DEPTH && comment.getReplies() != null) {
            replyResponses = new ArrayList<>();
            for (Comment reply : comment.getReplies()) {
                replyResponses.add(toResponse(reply, depth + 1));
            }
        } else {
            replyResponses = Collections.emptyList();
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getUser().getName())
                .authorEmail(comment.getUser().getEmail())
                .articleId(comment.getArticle().getId())
                .parentCommentId(comment.getParentComment() != null
                        ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .replies(replyResponses)
                .build();
    }
}
