package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizQuestionInvalidException extends BaseException {
    public QuizQuestionInvalidException() {
        super(QuizErrorCode.QUIZ_500_QUESTION_INVALID);
    }
}
