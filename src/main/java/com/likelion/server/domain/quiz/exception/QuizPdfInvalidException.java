package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizPdfInvalidException extends BaseException {
    public QuizPdfInvalidException() {
        super(QuizErrorCode.QUIZ_500_PDF_INVALID);
    }
}
