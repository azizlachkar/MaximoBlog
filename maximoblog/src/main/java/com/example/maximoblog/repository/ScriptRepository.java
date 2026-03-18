package com.example.maximoblog.repository;

import com.example.maximoblog.entity.Script;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Long> {

    Page<Script> findByCategory(String category, Pageable pageable);

    Page<Script> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT s FROM Script s WHERE " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Script> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
