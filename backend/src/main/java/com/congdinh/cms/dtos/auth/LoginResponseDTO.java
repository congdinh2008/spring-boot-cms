package com.congdinh.cms.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for authentication response containing JWT token and user information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response containing JWT token and user information")
public class LoginResponseDTO {

    @Schema(description = "JWT access token for authenticating subsequent requests", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token type, always 'Bearer'", example = "Bearer")
    private String tokenType;

    @Schema(description = "Token expiration time in seconds", example = "3600")
    private Long expiresIn;

    @Schema(description = "Authenticated user information")
    private UserInformation user;
}
