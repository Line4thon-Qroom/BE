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
}
