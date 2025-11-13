package com.likelion.server.domain.quiz.web.dto;

import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.enums.Difficulty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateQuizResponse {
    private Long id;
    private Long pdf_id;
    private int round;
    private String difficulty;
    private List<String> question_types;
    private int total_questions;
    private QaBoardInfo qa_board;

    @Getter
    @Builder
    public static class QaBoardInfo {
        private Long board_id;
        private String title;
    }

    public static CreateQuizResponse fromEntity(Quiz quiz, QaBoard qaBoard) {
        return CreateQuizResponse.builder()
                .id(quiz.getId())
                .pdf_id(quiz.getPdf() != null ? quiz.getPdf().getId() : null)
                .round(quiz.getRound())
                .difficulty(quiz.getDifficulty() != null ? quiz.getDifficulty().name() : null)
                .question_types(List.of(quiz.getQuestionTypes().split(",")))
                .total_questions(quiz.getTotalQuestions())
                .qa_board(
                        QaBoardInfo.builder()
                                .board_id(qaBoard.getId())
                                .title(qaBoard.getBoardName())
                                .build()
                )
                .build();
    }
}

