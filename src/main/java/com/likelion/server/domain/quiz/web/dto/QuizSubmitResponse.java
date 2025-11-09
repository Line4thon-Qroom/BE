package com.likelion.server.domain.quiz.web.dto;

import lombok.Builder;

@Builder
public record QuizSubmitResponse(
        Long quiz_result_id,
        int score,
        int correct_count,
        int total_questions,
        String status
) {
    public static QuizSubmitResponse fromEntity(
            Long quizResultId,
            int score,
            int correctCount,
            int totalQuestions
    ) {
        return QuizSubmitResponse.builder()
                .quiz_result_id(quizResultId)
                .score(score)
                .correct_count(correctCount)
                .total_questions(totalQuestions)
                .status("completed")
                .build();
    }
}
