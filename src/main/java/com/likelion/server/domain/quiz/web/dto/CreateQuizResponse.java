package com.likelion.server.domain.quiz.web.dto;

import com.likelion.server.domain.quiz.entity.enums.Difficulty;
import lombok.Builder;
import java.util.List;

@Builder
public record CreateQuizResponse(
        Long id,
        Long pdf_id,
        Integer round,
        Difficulty difficulty,
        List<String> question_types,
        Integer total_questions,
        String status,
        QaBoard qa_board
) {
    @Builder
    public record QaBoard(
            Long board_id,
            String title
    ) {}
}
