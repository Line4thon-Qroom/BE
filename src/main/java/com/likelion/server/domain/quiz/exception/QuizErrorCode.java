
package com.likelion.server.domain.quiz.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.*;

@Getter
@AllArgsConstructor
public enum QuizErrorCode implements BaseResponseCode {

    // 조회/데이터 관련
    QUIZ_404_NOT_FOUND("QUIZ_404_NOT_FOUND", NOT_FOUND, "해당 퀴즈를 찾을 수 없습니다."),
    QUIZ_RESULT_404_NOT_FOUND("QUIZ_RESULT_404_NOT_FOUND", NOT_FOUND, "해당 퀴즈 결과를 찾을 수 없습니다."),
    QUIZ_USER_ANSWER_NOT_FOUND("QUIZ_ANSWER_404_NOT_FOUND", NOT_FOUND, "해당 문항에 대한 답변 기록을 찾을 수 없습니다."),
    QUIZ_404_GROUP_NOT_FOUND("QUIZ_404_GROUP_NOT_FOUND", NOT_FOUND, "퀴즈가 속한 그룹 정보를 찾을 수 없습니다."),


    // 요청/입력 관련
    QUIZ_INVALID_REQUEST("QUIZ_400_INVALID_REQUEST", BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    QUIZ_400_INVALID_QUESTION_NUMBER("QUIZ_400_INVALID_QUESTION_NUMBER", BAD_REQUEST, "유효하지 않은 문제 번호입니다."),
    QUIZ_DUPLICATE_ANSWER("QUIZ_400_DUPLICATE_ANSWER", BAD_REQUEST, "이미 응답한 문제입니다."),

    // PDF / 파일 관련
    QUIZ_500_INVALID_FORMAT("QUIZ_500_INVALID_FORMAT", INTERNAL_SERVER_ERROR, "퀴즈 데이터 형식이 올바르지 않습니다."),
    QUIZ_500_PDF_INVALID("QUIZ_500_PDF_INVALID", INTERNAL_SERVER_ERROR, "PDF 파일이 비어있거나 손상되었습니다."),

    // AI 생성/파싱 관련
    QUIZ_AI_500_GENERATION_FAIL("QUIZ_AI_500_GENERATION_FAIL", INTERNAL_SERVER_ERROR, "AI 퀴즈 생성 중 오류가 발생했습니다."),
    QUIZ_AI_500_PARSE_FAIL("QUIZ_AI_500_PARSE_FAIL", INTERNAL_SERVER_ERROR, "AI 응답을 파싱하는 중 오류가 발생했습니다."),

    // DB / 문제 데이터 관련
    QUIZ_500_QUESTION_INVALID("QUIZ_500_QUESTION_INVALID", INTERNAL_SERVER_ERROR, "생성된 퀴즈 데이터가 불완전합니다."),
    QUIZ_500_SAVE_FAIL("QUIZ_500_SAVE_FAIL", INTERNAL_SERVER_ERROR, "퀴즈 결과 저장 중 오류가 발생했습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
