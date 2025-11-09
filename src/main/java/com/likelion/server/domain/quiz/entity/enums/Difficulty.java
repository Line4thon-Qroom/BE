package com.likelion.server.domain.quiz.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Difficulty {
    상("상"), 중("중"), 하("하");

    private final String korean;

    public static Difficulty fromKorean(String value) {
        return switch (value) {
            case "상" -> 상;
            case "중" -> 중;
            case "하" -> 하;
            default -> throw new IllegalArgumentException("잘못된 난이도 값: " + value);
        };
    }
}

