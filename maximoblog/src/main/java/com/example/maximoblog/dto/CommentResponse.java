package com.example.maximoblog.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private String authorName;
    private String authorEmail;
    private Long articleId;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private List<CommentResponse> replies;
}
