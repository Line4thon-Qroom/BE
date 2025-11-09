package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import static com.likelion.server.global.constant.StaticValue.NOT_FOUND;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuizErrorCode implements BaseResponseCode {
    QUIZ_NOT_FOUND("QUIZ_404_NOT_FOUND", NOT_FOUND, "해당 ID의 퀴즈를 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
