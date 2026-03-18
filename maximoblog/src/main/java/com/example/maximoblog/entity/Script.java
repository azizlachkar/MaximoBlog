package com.example.maximoblog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scripts", indexes = {
        @Index(name = "idx_script_category", columnList = "category"),
        @Index(name = "idx_script_author", columnList = "user_id"),
        @Index(name = "idx_script_views", columnList = "views")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Script {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String category;

    @Column(nullable = false)
    @Builder.Default
    private Long views = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── Relationships ───────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
