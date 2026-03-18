package com.example.maximoblog.service;

import com.example.maximoblog.dto.ArticleRequest;
import com.example.maximoblog.dto.ArticleResponse;
import com.example.maximoblog.entity.Article;
import com.example.maximoblog.entity.User;
import com.example.maximoblog.repository.ArticleRepository;
import com.example.maximoblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // ── Create ──────────────────────────────────────────────────

    public ResponseEntity<?> create(ArticleRequest request, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .bannerImageUrl(request.getBannerImageUrl())
                .author(author)
                .build();

        Article saved = articleRepository.save(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    // ── Get All (paginated) ─────────────────────────────────────

    public ResponseEntity<?> getAll(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ArticleResponse> articles = articleRepository.findAll(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(articles);
    }

    // ── Get by ID ───────────────────────────────────────────────

    public ResponseEntity<?> getById(Long id) {
        return articleRepository.findById(id)
                .map(article -> ResponseEntity.ok((Object) toResponse(article)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Article not found")));
    }

    // ── Update ──────────────────────────────────────────────────

    public ResponseEntity<?> update(Long id, ArticleRequest request, String authorEmail) {
        Article article = articleRepository.findById(id).orElse(null);

        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Article not found"));
        }

        if (!article.getAuthor().getEmail().equals(authorEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only update your own articles"));
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCategory(request.getCategory());
        article.setBannerImageUrl(request.getBannerImageUrl());

        Article updated = articleRepository.save(article);
        return ResponseEntity.ok(toResponse(updated));
    }

    // ── Delete ──────────────────────────────────────────────────

    public ResponseEntity<?> delete(Long id, String authorEmail) {
        Article article = articleRepository.findById(id).orElse(null);

        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Article not found"));
        }

        if (!article.getAuthor().getEmail().equals(authorEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only delete your own articles"));
        }

        articleRepository.delete(article);
        return ResponseEntity.ok(Map.of("message", "Article deleted successfully"));
    }

    // ── Search ──────────────────────────────────────────────────

    public ResponseEntity<?> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ArticleResponse> results = articleRepository
                .searchByKeyword(keyword, pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(results);
    }

    // ── Get by Category ─────────────────────────────────────────

    public ResponseEntity<?> getByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ArticleResponse> results = articleRepository
                .findByCategory(category, pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(results);
    }

    // ── Mapper ──────────────────────────────────────────────────

    private ArticleResponse toResponse(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .category(article.getCategory())
                .bannerImageUrl(article.getBannerImageUrl())
                .authorName(article.getAuthor().getName())
                .authorEmail(article.getAuthor().getEmail())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}
