package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizInvalidFormatException extends BaseException {
    public QuizInvalidFormatException() {
        super(QuizErrorCode.QUIZ_500_INVALID_FORMAT);
    }
}
