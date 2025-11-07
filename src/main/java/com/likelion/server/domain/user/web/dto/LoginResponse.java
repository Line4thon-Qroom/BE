package com.likelion.server.domain.user.web.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
    private UserResponse user;
}

