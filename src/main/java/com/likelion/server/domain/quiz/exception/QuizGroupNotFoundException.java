package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizGroupNotFoundException extends BaseException {
    public QuizGroupNotFoundException() {
        super(QuizErrorCode.QUIZ_404_GROUP_NOT_FOUND);
    }
}
