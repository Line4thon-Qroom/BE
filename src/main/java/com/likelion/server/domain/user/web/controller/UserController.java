package com.likelion.server.domain.user.web.controller;

import com.likelion.server.domain.user.service.UserService;
import com.likelion.server.domain.user.web.dto.*;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/home")
    public SuccessResponse<HomeResponse> getHome(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        HomeResponse data = userService.getHome(userId);
        return SuccessResponse.ok(data);
    }

    @GetMapping("/mypage")
    public SuccessResponse<MyPageResponse> getMyPage(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        MyPageResponse data = userService.getMyPage(userId);
        return SuccessResponse.ok(data);
    }

    @GetMapping("/mypage/wrong-note/{quiz_id}")
    public SuccessResponse<WrongNoteDetailResponse> getWrongNoteDetail(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("quiz_id") Long quizId
    ) {
        WrongNoteDetailResponse data = userService.getWrongNoteDetail(userId, quizId);
        return SuccessResponse.ok(data);
    }

    @PatchMapping("/mypage/wrong-note/{question_id}/review") //
    public SuccessResponse<UpdateWrongNoteResponse> updateWrongNote(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("question_id") Long questionId,
            @Valid @RequestBody UpdateWrongNoteRequest request
    ) {
        UpdateWrongNoteResponse data = userService.updateWrongNote(userId, questionId, request);
        return SuccessResponse.ok(data);
    }

    @PatchMapping("/mypage/profile")
    public SuccessResponse<UserResponse> updateProfile(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserResponse data = userService.updateProfile(userId, request);
        return SuccessResponse.ok(data);
    }
}
