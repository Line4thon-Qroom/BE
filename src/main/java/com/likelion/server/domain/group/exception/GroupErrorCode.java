package com.likelion.server.domain.group.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.*;

@Getter
@AllArgsConstructor
public enum GroupErrorCode implements BaseResponseCode {
    GROUP_404_NOT_FOUND_BY_CODE("GROUP_404_NOT_FOUND_BY_CODE", NOT_FOUND, "존재하지 않는 그룹 코드입니다."),
    GROUP_409_ALREADY_MEMBER("GROUP_409_ALREADY_MEMBER", CONFLICT, "이미 가입된 그룹입니다."),
    GROUP_403_PERMISSION_DENIED("GROUP_403_PERMISSION_DENIED", FORBIDDEN, "이 작업을 수행할 권한이 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
