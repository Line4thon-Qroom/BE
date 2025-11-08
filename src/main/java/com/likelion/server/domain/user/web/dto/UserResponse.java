package com.likelion.server.domain.user.web.dto;

import lombok.*;

/**
 * 회원가입 / 로그인 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String nickname;
}