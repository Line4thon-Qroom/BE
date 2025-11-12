package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizSaveFailException extends BaseException {
    public QuizSaveFailException() {
        super(QuizErrorCode.QUIZ_500_SAVE_FAIL);
    }
}
