package com.congdinh.cms.services;

import com.congdinh.cms.dtos.auth.LoginRequestDTO;
import com.congdinh.cms.dtos.auth.LoginResponseDTO;
import com.congdinh.cms.dtos.auth.RegisterRequestDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    LoginResponseDTO register(RegisterRequestDTO registerRequest);
}
