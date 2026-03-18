package com.example.maximoblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScriptRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "Code is required")
    private String code;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    @Size(max = 100, message = "Category must be at most 100 characters")
    private String category;
}
