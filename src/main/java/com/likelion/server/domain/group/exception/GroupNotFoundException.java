package com.likelion.server.domain.group.exception;

import com.likelion.server.global.exception.BaseException;

public class GroupNotFoundException extends BaseException {
    public GroupNotFoundException() {
        super(GroupErrorCode.GROUP_404_NOT_FOUND_BY_CODE);
    }
}
