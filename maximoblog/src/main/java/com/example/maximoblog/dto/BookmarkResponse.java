package com.example.maximoblog.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkResponse {

    private Long id;
    private Long articleId;
    private String articleTitle;
    private String articleCategory;
    private String authorName;
    private LocalDateTime bookmarkedAt;
}
