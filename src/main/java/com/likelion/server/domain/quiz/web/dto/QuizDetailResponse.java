package com.likelion.server.domain.quiz.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizDetailResponse {

    private QuizInfo quiz;
    private List<QuestionInfo> questions;

    @Getter
    @AllArgsConstructor
    public static class QuizInfo {
        private Long id;
        private Long pdf_id;
        private String title;
        private String difficulty;
        private int round;
        private int total_questions;
        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    public static class QuestionInfo {
        private Long id;
        private String type;
        private String question_text;
        private String correct_answer;
        private String explanation;
    }
}

