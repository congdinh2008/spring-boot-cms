package com.congdinh.cms.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for user registration request.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for user registration")
public class RegisterRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^\\w+$", message = "Username can only contain letters, numbers, and underscores")
    @Schema(description = "Unique username for the account. Only letters, numbers, and underscores allowed.", example = "johndoe", minLength = 3, maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Unique email address for the account", example = "johndoe@example.com", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)")
    @Schema(description = "Strong password with at least one uppercase, one lowercase, one digit, and one special character (@$!%*?&)", example = "Password@123", minLength = 8, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Schema(description = "Must match the password field", example = "Password@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
}
