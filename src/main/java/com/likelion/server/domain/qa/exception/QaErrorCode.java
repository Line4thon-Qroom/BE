package com.likelion.server.domain.qa.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum QaErrorCode implements BaseResponseCode {
    QA_404_NOT_FOUND_BY_CODE("QA_404_NOT_FOUND_BY_CODE", NOT_FOUND, "해당 ID의 게시판을 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}

