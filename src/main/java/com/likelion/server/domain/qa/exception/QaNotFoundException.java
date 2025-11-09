package com.likelion.server.domain.qa.exception;


import com.likelion.server.global.exception.BaseException;

public class QaNotFoundException extends BaseException {
    public QaNotFoundException() { super(QaErrorCode.QA_404_NOT_FOUND_BY_CODE); }
}
