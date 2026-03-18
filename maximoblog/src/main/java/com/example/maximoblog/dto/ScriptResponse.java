package com.example.maximoblog.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScriptResponse {

    private Long id;
    private String title;
    private String code;
    private String description;
    private String category;
    private Long views;
    private String authorName;
    private String authorEmail;
    private LocalDateTime createdAt;
}
