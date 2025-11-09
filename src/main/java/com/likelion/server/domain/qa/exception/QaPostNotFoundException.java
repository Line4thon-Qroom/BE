package com.likelion.server.domain.qa.exception;

import com.likelion.server.global.exception.BaseException;

public class QaPostNotFoundException extends BaseException {
    public QaPostNotFoundException() { super(QaErrorCode.QA_POST_NOT_FOUND); }
}
