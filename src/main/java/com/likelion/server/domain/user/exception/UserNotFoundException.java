package com.likelion.server.domain.user.exception;

import com.likelion.server.global.exception.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_404_NOT_FOUND);
    }
}
