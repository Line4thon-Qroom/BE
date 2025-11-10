package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizDuplicateAnswerException extends BaseException {
    public QuizDuplicateAnswerException() {
        super(QuizErrorCode.QUIZ_DUPLICATE_ANSWER);
    }
}
