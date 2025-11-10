package com.likelion.server.domain.user.service;

import com.likelion.server.domain.user.web.dto.*;

public interface UserService {
    UserResponse signup(SignupRequest request); // 회원가입

    LoginResponse login(LoginRequest request); // 로그인

    HomeResponse getHome(Long userId); // 홈화면

    MyPageResponse getMyPage(Long userId); // 마이페이지

    WrongNoteDetailResponse getWrongNoteDetail(Long userId, Long quizId); // 오답노트 조회

    UpdateWrongNoteResponse updateWrongNote(Long userId, Long questionId, UpdateWrongNoteRequest request); // 오답노트 작성/수정

    UserResponse updateProfile(Long userId, UpdateProfileRequest request); // 개인정보 변경
}
