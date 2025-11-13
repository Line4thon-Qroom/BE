package com.likelion.server.domain.quiz.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserQuizRequest {
    private Long group_id;
    private String title;
    private List<UserQuestionRequest> questions;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserQuestionRequest {
        private String type;  // "OX", "객관식", "단답형"
        private Integer question_number;
        private String question_text;
        private String correct_answer;
        private String explanation;
        private List<OptionRequest> options; // 객관식일 때만 존재
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionRequest {
        private String option_text;
    }
}
