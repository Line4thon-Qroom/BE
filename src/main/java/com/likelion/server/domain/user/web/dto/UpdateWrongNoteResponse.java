package com.likelion.server.domain.user.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.server.domain.quiz.entity.QuizUserAnswer;
import java.time.format.DateTimeFormatter;

public record UpdateWrongNoteResponse(
        @JsonProperty("question_id")
        Long questionId,

        ReviewDto review
) {
    public record ReviewDto(
            String memo,

            @JsonProperty("review_updated_at")
            String reviewUpdatedAt
    ) {
        public ReviewDto(QuizUserAnswer answer) {
            this(
                    answer.getMemo(),
                    (answer.getReviewUpdatedAt() != null)
                            ? answer.getReviewUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            : null
            );
        }
    }

    public UpdateWrongNoteResponse(QuizUserAnswer answer) {
        this(
                answer.getQuestion().getId(),
                new ReviewDto(answer)
        );
    }
}
