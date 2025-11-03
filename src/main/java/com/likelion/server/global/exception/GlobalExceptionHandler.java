package com.likelion.server.global.exception;

import com.likelion.server.global.response.ErrorResponse;
import com.likelion.server.global.response.code.GlobalErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
        javax.validation.Valid or @Validated 으로 binding error 발생시 발생
        주로 @RequestBody, @RequestPart 어노테이션에서 발생
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.INVALID_HTTP_MESSAGE_BODY,
                e.getFieldError().getDefaultMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* binding error 발생시 BindException 발생 */
    @ExceptionHandler(BindException.class)
    private ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.INVALID_HTTP_MESSAGE_BODY);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* enum type 일치하지 않아 binding 못할 경우 발생 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.INVALID_HTTP_MESSAGE_BODY);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* 지원하지 않은 HTTP method 호출 할 경우 발생 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.UNSUPPORTED_HTTP_METHOD);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* HTTP 요청 바디(JSON) 파싱 중 Enum 변환 실패 또는 다른 형식 오류 처리 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException", e);

        // Jackson이 Enum 매핑 실패 시 InvalidFormatException을 던짐
        if (e.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife
                && ife.getTargetType() != null
                && ife.getTargetType().isEnum()) {
            ErrorResponse error = ErrorResponse.of(GlobalErrorCode.GLOBAL_INVALID_ENUM);
            return ResponseEntity.status(error.getHttpStatus()).body(error);
        }

        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.BAD_REQUEST_ERROR);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }


    /* 비지니스 로직 에러 */
    @ExceptionHandler(BaseException.class)
    private ResponseEntity<ErrorResponse> handleBusinessException(BaseException e) {
        log.error("BusinessError ");
        log.error(e.getErrorCode().getMessage());
        ErrorResponse error = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* 나머지 예외 처리 */
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception Error ", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.SERVER_ERROR);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

}