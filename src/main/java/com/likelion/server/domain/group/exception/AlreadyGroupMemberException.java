package com.likelion.server.domain.group.exception;

import com.likelion.server.global.exception.BaseException;

public class AlreadyGroupMemberException extends BaseException {
    public AlreadyGroupMemberException() {
        super(GroupErrorCode.GROUP_409_ALREADY_MEMBER);
    }
}
