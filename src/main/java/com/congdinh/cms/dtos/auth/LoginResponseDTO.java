package com.congdinh.cms.dtos.auth;

import lombok.*;

/**
 * DTO for authentication response containing JWT token and user information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    
    private String token;
    
    private String tokenType;
    
    private Long expiresIn;
    
    private UserInformation user;
}
