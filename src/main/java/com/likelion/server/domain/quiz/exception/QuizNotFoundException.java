package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizNotFoundException extends BaseException {
    public QuizNotFoundException() {
        super(QuizErrorCode.QUIZ_404_NOT_FOUND);
    }
}
