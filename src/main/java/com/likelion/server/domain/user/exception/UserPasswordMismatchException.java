package com.likelion.server.domain.user.exception;

import com.likelion.server.global.exception.BaseException;

public class UserPasswordMismatchException extends BaseException {
    public UserPasswordMismatchException() {
        super(UserErrorCode.USER_PASSWORD_MISMATCH);
    }
}
