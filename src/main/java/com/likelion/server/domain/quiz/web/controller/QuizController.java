package com.likelion.server.domain.quiz.web.controller;

import com.likelion.server.domain.quiz.service.QuizService;
import com.likelion.server.domain.quiz.web.dto.CreateQuizRequest;
import com.likelion.server.domain.quiz.web.dto.CreateQuizResponse;
import com.likelion.server.domain.quiz.web.dto.CreateUserQuizRequest;
import com.likelion.server.domain.quiz.web.dto.QuizDetailResponse;
import com.likelion.server.domain.quiz.web.dto.QuizQaResponse;
import com.likelion.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    // AI 생성
    @PostMapping("/create")
    public SuccessResponse<CreateQuizResponse> createQuiz(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody CreateQuizRequest request
    ) {
        CreateQuizResponse data = quizService.createQuiz(userId, request);
        return SuccessResponse.created(data);
    }

    // 사용자 직접 생성
    @PostMapping("/user/create")
    public SuccessResponse<CreateQuizResponse> createUserQuiz(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody CreateUserQuizRequest request
    ) {
        CreateQuizResponse data = quizService.createUserQuiz(userId, request);
        return SuccessResponse.created(data);
    }


    // 퀴즈 조회
    @GetMapping("/{quiz_id}")
    public SuccessResponse<QuizDetailResponse> getQuizDetail(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("quiz_id") Long quizId
    ) {
        QuizDetailResponse data = quizService.getQuizDetail(quizId);
        return SuccessResponse.ok(data);
    }

    // 시험지+QA 게시판 통합 조회
    @GetMapping("/{quiz_id}/qa-room")
    public SuccessResponse<QuizQaResponse> getQuizWithQaRoom(
            @PathVariable("quiz_id") Long quizId
    ) {
        QuizQaResponse data = quizService.getQuizWithQa(quizId);
        return SuccessResponse.ok(data);
    }
}
