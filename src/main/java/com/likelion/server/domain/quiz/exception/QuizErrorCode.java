package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.NOT_FOUND;
import static com.likelion.server.global.constant.StaticValue.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum QuizErrorCode implements BaseResponseCode {

    QUIZ_404_NOT_FOUND("QUIZ_404_NOT_FOUND", NOT_FOUND, "해당 퀴즈를 찾을 수 없습니다."),
    QUIZ_RESULT_404_NOT_FOUND("QUIZ_RESULT_404_NOT_FOUND", NOT_FOUND, "해당 퀴즈 결과를 찾을 수 없습니다."),
    QUIZ_INVALID_REQUEST("QUIZ_400_INVALID_REQUEST", BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    QUIZ_DUPLICATE_ANSWER("QUIZ_400_DUPLICATE_ANSWER", BAD_REQUEST, "이미 응답한 문제입니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
