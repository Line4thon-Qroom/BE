package com.likelion.server.domain.user.service;

import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import com.likelion.server.domain.user.web.dto.*;
import com.likelion.server.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 회원가입 / 로그인 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    //1. 회원가입
    @Override
    public UserResponse signup(SignupRequest request) {
        // 중복 닉네임 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 일치 검사
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 유저 생성 및 저장
        User user = User.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getNickname());
    }


    //2. 로그인
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 닉네임입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // Access / Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getNickname());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // Refresh Token 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // DTO 형태로 반환
        return new LoginResponse(
                "로그인 성공",
                accessToken,
                refreshToken,
                new UserResponse(user.getId(), user.getNickname())
        );
    }
}
