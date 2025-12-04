package com.congdinh.cms.dtos.auth;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO containing user information for API responses.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile information")
public class UserInformation {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "Username of the user", example = "johndoe")
    private String username;

    @Schema(description = "Email address of the user", example = "johndoe@example.com")
    private String email;

    @Schema(description = "Account status", example = "ACTIVE", allowableValues = { "ACTIVE", "INACTIVE" })
    private String status;

    @Schema(description = "List of roles assigned to the user", example = "[\"ROLE_REPORTER\"]")
    private Set<String> roles;
}
