package com.likelion.server.domain.quiz.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Type {
    OX("OX"),
    MULTIPLE_CHOICE("객관식"),
    SHORT_ANSWER("단답형");

    private final String displayName;

    Type(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue // ← JSON 직렬화 시 이 값을 사용
    public String getDisplayName() {
        return displayName;
    }

    public static Type fromKorean(String value) {
        return switch (value) {
            case "객관식" -> MULTIPLE_CHOICE;
            case "단답형" -> SHORT_ANSWER;
            case "OX", "O,X", "O X" -> OX; // OX 입력 변형도 허용
            default -> throw new IllegalArgumentException("잘못된 문제 유형: " + value);
        };
    }
}
