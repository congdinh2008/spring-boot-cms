package com.congdinh.cms.dtos.category;

import java.time.LocalDateTime;

import lombok.*;

/**
 * DTO for category response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {
    
    private Long id;
    
    private String name;
    
    private String slug;
    
    private LocalDateTime createdAt;
}
