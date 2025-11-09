package com.likelion.server.domain.quiz.web.controller;

import com.likelion.server.domain.quiz.service.QuizResultService;
import com.likelion.server.domain.quiz.web.dto.QuizResultRequest;
import com.likelion.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizResultController {

    private final QuizResultService quizResultService;

    @PostMapping("/start")
    public SuccessResponse<Map<String, Object>> startQuiz(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody QuizResultRequest request
    ) {
        var result = quizResultService.startQuiz(userId, request);

        return SuccessResponse.ok(Map.of(
                "message", "퀴즈 응시를 시작합니다.",
                "quiz_result_id", result.getId()
        ));
    }
}
