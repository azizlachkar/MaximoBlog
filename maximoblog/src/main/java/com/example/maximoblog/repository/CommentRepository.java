package com.example.maximoblog.repository;

import com.example.maximoblog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Top-level comments for an article (no parent), newest first
    List<Comment> findByArticleIdAndParentCommentIsNullOrderByCreatedAtDesc(Long articleId);

    List<Comment> findByUserId(Long userId);

    long countByArticleId(Long articleId);
}
