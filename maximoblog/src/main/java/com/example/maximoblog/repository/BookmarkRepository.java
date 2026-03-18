package com.example.maximoblog.repository;

import com.example.maximoblog.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUserId(Long userId);

    Optional<Bookmark> findByUserIdAndArticleId(Long userId, Long articleId);

    boolean existsByUserIdAndArticleId(Long userId, Long articleId);
}
