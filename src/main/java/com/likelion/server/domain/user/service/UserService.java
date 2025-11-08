package com.likelion.server.domain.user.service;

import com.likelion.server.domain.user.web.dto.*;

import java.util.Map;

public interface UserService {
    UserResponse signup(SignupRequest request); // 회원가입

    LoginResponse login(LoginRequest request); // 로그인
}
