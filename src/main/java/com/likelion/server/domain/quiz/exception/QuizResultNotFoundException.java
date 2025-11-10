package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizResultNotFoundException extends BaseException {
    public QuizResultNotFoundException() {
        super(QuizErrorCode.QUIZ_RESULT_404_NOT_FOUND);
    }
}
