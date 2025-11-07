package com.likelion.server.domain.user.web.controller;

import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import com.likelion.server.domain.user.service.UserService;
import com.likelion.server.domain.user.web.dto.*;
import com.likelion.server.global.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 회원가입 / 로그인 API
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse user = userService.signup(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "회원가입이 완료되었습니다.");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    // 2. 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }


    // 3. 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        // DB에서 Refresh Token 일치하는 사용자 조회
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token"));

        // Refresh Token 자체가 만료되었는지 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("만료된 Refresh Token입니다. 다시 로그인하세요.");
        }

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getNickname());

        // 응답 반환
        RefreshResponse response = new RefreshResponse(
                "Access Token 재발급 성공",
                newAccessToken
        );

        return ResponseEntity.ok(response);
    }

}
