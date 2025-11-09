package com.likelion.server.domain.quiz.web.controller;

import com.likelion.server.domain.quiz.service.QuizSubmitService;
import com.likelion.server.domain.quiz.web.dto.QuizSubmitRequest;
import com.likelion.server.domain.quiz.web.dto.QuizSubmitResponse;
import com.likelion.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizSubmitController {

    private final QuizSubmitService quizSubmitService;

    @PostMapping("/submit")
    public SuccessResponse<?> submitQuiz(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody QuizSubmitRequest request
    ) {
        QuizSubmitResponse response = quizSubmitService.submitQuiz(userId, request);
        return SuccessResponse.ok(Map.of(
                "message", "채점이 완료되었습니다.",
                "result", response
        ));
    }
}

