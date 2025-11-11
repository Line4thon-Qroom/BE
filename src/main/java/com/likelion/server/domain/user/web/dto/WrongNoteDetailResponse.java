package com.likelion.server.domain.user.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.server.domain.quiz.entity.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record WrongNoteDetailResponse(
        QuizSummaryDto quiz,
        @JsonProperty("wrong_questions") List<WrongQuestionDto> wrongQuestions
) {

    public record QuizSummaryDto(
            Long id,
            String title,
            @JsonProperty("group_name") String groupName,
            Integer round,
            String difficulty,
            Integer score,
            @JsonProperty("total_questions") Integer totalQuestions,
            Long accuracy
    ) {
        public static QuizSummaryDto from(QuizResult result) {
            Quiz quiz = result.getQuiz();
            int total = (quiz.getTotalQuestions() != null) ? quiz.getTotalQuestions() : 0;
            int correct = (result.getCorrectCount() != null) ? result.getCorrectCount() : 0;

            long calculatedAccuracy = 0;
            if (total > 0) {
                calculatedAccuracy = Math.round((double) correct * 100 / total);
            }

            return new QuizSummaryDto(
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getGroup().getName(),
                    quiz.getRound(),
                    quiz.getDifficulty().getKorean(),
                    result.getScore(),
                    total,
                    calculatedAccuracy
            );
        }
    }
    // 틀린 문제 상세 DTO
    public record WrongQuestionDto(
            @JsonProperty("quiz_question_id")
            Long id,
            String type,
            @JsonProperty("question_text") String questionText,
            @JsonProperty("user_answer") String userAnswer,
            @JsonProperty("correct_answer") String correctAnswer,
            @JsonProperty("is_correct") Boolean isCorrect,
            String explanation,
            @JsonProperty("review_note") String reviewNote,
            List<OptionDto> options
    ) {
        public static WrongQuestionDto from(QuizUserAnswer userAnswer, List<QuizOption> options) {
            QuizQuestion question = userAnswer.getQuestion();

            List<OptionDto> optionDtos = Collections.emptyList();
            if (options != null) {
                optionDtos = options.stream().map(OptionDto::new).collect(Collectors.toList());
            }

            return new WrongQuestionDto(
                    question.getId(),
                    question.getType().toString(),
                    question.getQuestionText(),
                    userAnswer.getUserAnswer(),
                    question.getCorrectAnswer(),
                    userAnswer.getIsCorrect(),
                    question.getExplanation(),
                    userAnswer.getMemo(),
                    optionDtos
            );
        }
    }

    // 객관식보기 DTO
    public record OptionDto(
            Long id,
            @JsonProperty("option_text") String optionText
    ) {
        public OptionDto(QuizOption option) {
            this(option.getId(), option.getOptionText());
        }
    }

    public WrongNoteDetailResponse(QuizResult quizResult, List<WrongQuestionDto> wrongQuestionDtos) {
        this(
                QuizSummaryDto.from(quizResult),
                wrongQuestionDtos
        );
    }
}
