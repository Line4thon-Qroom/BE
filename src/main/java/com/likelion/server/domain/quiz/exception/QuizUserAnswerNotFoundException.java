package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.exception.BaseException;

public class QuizUserAnswerNotFoundException extends BaseException {
  public QuizUserAnswerNotFoundException() {
    super(QuizErrorCode.QUIZ_USER_ANSWER_NOT_FOUND);
  }
}
