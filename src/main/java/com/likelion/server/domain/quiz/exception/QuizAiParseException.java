package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizAiParseException extends BaseException {
    public QuizAiParseException() {
        super(QuizErrorCode.QUIZ_AI_500_PARSE_FAIL);
    }
}
