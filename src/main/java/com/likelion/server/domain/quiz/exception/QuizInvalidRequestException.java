package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizInvalidRequestException extends BaseException {
    public QuizInvalidRequestException() {
        super(QuizErrorCode.QUIZ_INVALID_REQUEST);
    }
}
