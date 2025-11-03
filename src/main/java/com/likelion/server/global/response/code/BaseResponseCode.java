package com.likelion.server.global.response.code;

public interface BaseResponseCode {
    String getCode();
    String getMessage();
    int getHttpStatus();
}