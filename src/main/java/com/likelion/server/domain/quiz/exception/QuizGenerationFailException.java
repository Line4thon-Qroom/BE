package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizGenerationFailException extends BaseException {
    public QuizGenerationFailException() {
        super(QuizErrorCode.QUIZ_AI_500_GENERATION_FAIL);
    }
}
