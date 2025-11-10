package com.likelion.server.domain.user.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.user.entity.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record MyPageResponse(
        UserDto user,
        @JsonProperty("wrong_notes") List<WrongNoteDto> wrongNotes
) {

    // 사용자 정보
    public record UserDto(
            Long id,
            String nickname
    ) {
        public UserDto(User user) {
            this(
                    user.getId(),
                    user.getNickname()
            );
        }
    }

    // 오답노트
    public record WrongNoteDto(
            @JsonProperty("quiz_id") Long quizId,
            @JsonProperty("quiz_title") String quizTitle,
            @JsonProperty("correct_count") Integer correctCount,
            @JsonProperty("total_questions") Integer totalQuestions,
            Long accuracy,
            @JsonProperty("last_attempt_date") String lastAttemptDate
    ) {
        public static WrongNoteDto from(QuizResult result) {
            Quiz quiz = result.getQuiz();
            int total = (quiz.getTotalQuestions() != null) ? quiz.getTotalQuestions() : 0;
            int correct = (result.getCorrectCount() != null) ? result.getCorrectCount() : 0;

            long calculatedAccuracy = 0;
            if (total > 0) {
                calculatedAccuracy = Math.round((double) correct * 100 / total);
            }

            return new WrongNoteDto(
                    quiz.getId(),
                    quiz.getTitle(),
                    correct,
                    total,
                    calculatedAccuracy, // "accuracy": 95
                    result.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // "last_attempt_date"
            );
        }
    }

    public MyPageResponse(User user, List<QuizResult> results) {
        this(
                new UserDto(user),
                results.stream()
                        .map(WrongNoteDto::from)
                        .toList()
        );
    }
}
