package com.likelion.server.domain.quiz.web.dto;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.user.entity.User;
import lombok.Builder;

@Builder
public record QuizResultRequest(
        Long quiz_id
) {
    public QuizResult toEntity(Quiz quiz, User user, Group group) {
        return QuizResult.builder()
                .quiz(quiz)
                .user(user)
                .group(group)
                .score(0)
                .correctCount(0)
                .build();
    }
}
