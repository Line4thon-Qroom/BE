package com.likelion.server.domain.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ✅ Access Token 재발급 응답 DTO
 */
@Getter
@AllArgsConstructor
public class RefreshResponse {
    private String message;
    private String accessToken;
}
