package com.example.maximoblog.service;

import com.example.maximoblog.dto.BookmarkResponse;
import com.example.maximoblog.entity.Article;
import com.example.maximoblog.entity.Bookmark;
import com.example.maximoblog.entity.User;
import com.example.maximoblog.repository.ArticleRepository;
import com.example.maximoblog.repository.BookmarkRepository;
import com.example.maximoblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // ── Add Bookmark ────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> addBookmark(Long articleId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        if (bookmarkRepository.existsByUserIdAndArticleId(user.getId(), articleId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Article is already bookmarked"));
        }

        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Article not found"));
        }

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .article(article)
                .build();

        Bookmark saved = bookmarkRepository.save(bookmark);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    // ── Remove Bookmark ─────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> removeBookmark(Long articleId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        Bookmark bookmark = bookmarkRepository.findByUserIdAndArticleId(user.getId(), articleId).orElse(null);
        if (bookmark == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Bookmark not found"));
        }

        bookmarkRepository.delete(bookmark);
        return ResponseEntity.ok(Map.of("message", "Bookmark removed successfully"));
    }

    // ── Get User Bookmarks ──────────────────────────────────────

    @Transactional(readOnly = true)
    public ResponseEntity<?> getUserBookmarks(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        List<BookmarkResponse> bookmarks = bookmarkRepository.findByUserId(user.getId())
                .stream()
                .sorted(Comparator.comparing(Bookmark::getCreatedAt).reversed())
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookmarks);
    }

    // ── Helper ──────────────────────────────────────────────────

    private BookmarkResponse toResponse(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .articleId(bookmark.getArticle().getId())
                .articleTitle(bookmark.getArticle().getTitle())
                .articleCategory(bookmark.getArticle().getCategory())
                .authorName(bookmark.getArticle().getAuthor().getName())
                .bookmarkedAt(bookmark.getCreatedAt())
                .build();
    }
}
