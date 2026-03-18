package com.example.maximoblog.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String category;
    private String bannerImageUrl;
    private String authorName;
    private String authorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
