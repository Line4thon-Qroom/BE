package com.likelion.server.domain.quiz.web.dto;

import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizOption;
import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.quiz.entity.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizDetailResponse {

    private QuizInfo quiz;
    private List<QuestionInfo> questions;

    // 퀴즈 기본 정보
    @Getter
    @Builder
    @AllArgsConstructor
    public static class QuizInfo {
        private Long id;
        private Long pdf_id;
        private String title;
        private String difficulty;
        private int round;
        private int total_questions;
        private LocalDateTime createdAt;

        public static QuizInfo fromEntity(Quiz quiz) {
            return QuizInfo.builder()
                    .id(quiz.getId())
                    .pdf_id(quiz.getPdf().getId())
                    .title(quiz.getPdf().getFileName())
                    .difficulty(quiz.getDifficulty().name())
                    .round(quiz.getRound())
                    .total_questions(quiz.getTotalQuestions())
                    .createdAt(quiz.getCreatedAt())
                    .build();
        }
    }


    // 퀴즈 문제
    @Builder
    @Getter
    @AllArgsConstructor
    public static class QuestionInfo {
        private Long id;
        private String type;
        private String question_text;
        private String correct_answer;
        private String explanation;
        private List<OptionInfo> options;

        public static QuestionInfo fromEntity(QuizQuestion q) {
            return QuestionInfo.builder()
                    .id(q.getId())
                    .type(convertTypeToKorean(q.getType()))
                    .question_text(q.getQuestionText())
                    .correct_answer(q.getCorrectAnswer())
                    .explanation(q.getExplanation())
                    .options(q.getOptions().stream()
                            .map(OptionInfo::fromEntity)
                            .collect(Collectors.toList()))
                    .build();
        }

        static String convertTypeToKorean(Type type) {
            return switch (type) {
                case OX -> "OX";
                case MULTIPLE_CHOICE -> "객관식";
                case SHORT_ANSWER -> "단답형";
            };
        }
    }

    // 객관식 보기
    @Getter
    @Builder
    public static class OptionInfo {
        private Long id;
        private String option_text;

        public static OptionInfo fromEntity(QuizOption o) {
            return OptionInfo.builder()
                    .id(o.getId())
                    .option_text(o.getOptionText())
                    .build();
        }
    }

    // 전체 DTO 변환 (Service에서 바로 호출)
    public static QuizDetailResponse fromEntities(Quiz quiz, List<QuizQuestion> questions) {
        return QuizDetailResponse.builder()
                .quiz(QuizInfo.fromEntity(quiz))
                .questions(questions.stream()
                        .map(QuestionInfo::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}

