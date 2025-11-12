package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizInvalidQuestionNumberException extends BaseException {
    public QuizInvalidQuestionNumberException() {
        super(QuizErrorCode.QUIZ_400_INVALID_QUESTION_NUMBER);
    }
}
