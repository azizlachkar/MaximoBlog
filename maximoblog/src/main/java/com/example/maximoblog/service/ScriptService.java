package com.example.maximoblog.service;

import com.example.maximoblog.dto.ScriptRequest;
import com.example.maximoblog.dto.ScriptResponse;
import com.example.maximoblog.entity.Script;
import com.example.maximoblog.entity.User;
import com.example.maximoblog.repository.ScriptRepository;
import com.example.maximoblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScriptService {

    private final ScriptRepository scriptRepository;
    private final UserRepository userRepository;

    // ── Create ──────────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> create(ScriptRequest request, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail).orElse(null);
        if (author == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        Script script = Script.builder()
                .title(request.getTitle())
                .code(request.getCode())
                .description(request.getDescription())
                .category(request.getCategory())
                .user(author)
                .build();

        Script saved = scriptRepository.save(script);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    // ── Get All (paginated) ─────────────────────────────────────

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAll(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ScriptResponse> scripts = scriptRepository.findAll(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(scripts);
    }

    // ── Get Popular Scripts ─────────────────────────────────────

    @Transactional(readOnly = true)
    public ResponseEntity<?> getPopular(int page, int size) {
        // Sort by views descending
        Pageable pageable = PageRequest.of(page, size, Sort.by("views").descending());

        Page<ScriptResponse> scripts = scriptRepository.findAll(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(scripts);
    }

    // ── Get by ID ───────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> getById(Long id) {
        Script script = scriptRepository.findById(id).orElse(null);
        if (script == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Script not found"));
        }

        // Increment views on every read
        script.setViews(script.getViews() + 1);
        scriptRepository.save(script);

        return ResponseEntity.ok(toResponse(script));
    }

    // ── Update ──────────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> update(Long id, ScriptRequest request, String authorEmail) {
        Script script = scriptRepository.findById(id).orElse(null);
        if (script == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Script not found"));
        }

        if (!script.getUser().getEmail().equals(authorEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only update your own scripts"));
        }

        script.setTitle(request.getTitle());
        script.setCode(request.getCode());
        script.setDescription(request.getDescription());
        script.setCategory(request.getCategory());

        Script updated = scriptRepository.save(script);
        return ResponseEntity.ok(toResponse(updated));
    }

    // ── Delete ──────────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> delete(Long id, String authorEmail) {
        Script script = scriptRepository.findById(id).orElse(null);
        if (script == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Script not found"));
        }

        if (!script.getUser().getEmail().equals(authorEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only delete your own scripts"));
        }

        scriptRepository.delete(script);
        return ResponseEntity.ok(Map.of("message", "Script deleted successfully"));
    }

    // ── Search ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ResponseEntity<?> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ScriptResponse> results = scriptRepository
                .searchByKeyword(keyword, pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(results);
    }

    // ── Get by Category ─────────────────────────────────────────

    @Transactional(readOnly = true)
    public ResponseEntity<?> getByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ScriptResponse> results = scriptRepository
                .findByCategory(category, pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(results);
    }

    // ── Mapper ──────────────────────────────────────────────────

    private ScriptResponse toResponse(Script script) {
        return ScriptResponse.builder()
                .id(script.getId())
                .title(script.getTitle())
                .code(script.getCode())
                .description(script.getDescription())
                .category(script.getCategory())
                .views(script.getViews())
                .authorName(script.getUser().getName())
                .authorEmail(script.getUser().getEmail())
                .createdAt(script.getCreatedAt())
                .build();
    }
}
