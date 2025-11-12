package com.likelion.server.domain.quiz.web.dto;

import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.quiz.entity.QuizUserAnswer;
import com.likelion.server.domain.user.entity.User;
import lombok.Builder;

import java.util.List;

@Builder
public record QuizSubmitRequest(
        Long quiz_result_id,
        List<Answer> answers
) {
    @Builder
    public record Answer(
            Integer question_number,
            String user_answer
    ) {
        public QuizUserAnswer toEntity(QuizResult result, QuizQuestion question, User user, boolean isCorrect) {
            return QuizUserAnswer.builder()
                    .quizResult(result)
                    .question(question)
                    .user(user)
                    .userAnswer(user_answer)
                    .isCorrect(isCorrect)
                    .build();
        }
    }
}
