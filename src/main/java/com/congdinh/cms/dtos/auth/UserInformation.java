package com.congdinh.cms.dtos.auth;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class UserInformation {
    
    private Long id;
    
    private String username;
    
    private String email;
    
    private String status;
    
    private Set<String> roles;
}
