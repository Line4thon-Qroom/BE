package com.likelion.server.domain.quiz.exception;

import lombok.Getter;

@Getter
public class QuizException extends RuntimeException {

    private final QuizErrorCode errorCode;

    public QuizException(QuizErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public static final QuizErrorCode QUIZ_NOT_FOUND = new QuizErrorCode("QUIZ_404", "퀴즈를 찾을 수 없습니다.");
    public static final QuizErrorCode QUIZ_RESULT_NOT_FOUND = new QuizErrorCode("QUIZ_RESULT_404", "퀴즈 결과를 찾을 수 없습니다.");

    @Getter
    public static class QuizErrorCode {
        private final String code;
        private final String message;

        public QuizErrorCode(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
