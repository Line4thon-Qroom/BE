package com.likelion.server.domain.user.exception;

import com.likelion.server.global.exception.BaseException;

public class UserNicknameDuplicatedException extends BaseException {
    public UserNicknameDuplicatedException() {
        super(UserErrorCode.USER_NICKNAME_DUPLICATED);
    }
}