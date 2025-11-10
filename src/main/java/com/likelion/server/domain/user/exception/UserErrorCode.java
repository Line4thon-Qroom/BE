package com.likelion.server.domain.user.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.*;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseResponseCode {
    USER_404_NOT_FOUND("USER_404_NOT_FOUND", NOT_FOUND, "해당 ID의 사용자를 찾을 수 없습니다."),
    USER_NICKNAME_DUPLICATED("USER_409_NICKNAME_DUPLICATED", CONFLICT, "이미 존재하는 닉네임입니다."),
    USER_PASSWORD_MISMATCH("USER_400_PASSWORD_MISMATCH", BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}