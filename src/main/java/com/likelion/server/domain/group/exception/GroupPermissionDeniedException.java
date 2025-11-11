package com.likelion.server.domain.group.exception;

import com.likelion.server.global.exception.BaseException;

public class GroupPermissionDeniedException extends BaseException {
  public GroupPermissionDeniedException() {
    super(GroupErrorCode.GROUP_403_PERMISSION_DENIED);
  }
}
