package com.likelion.server.domain.quiz.web.dto;

import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.quiz.entity.QuizUserAnswer;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.likelion.server.domain.quiz.web.dto.QuizDetailResponse.QuestionInfo.convertTypeToKorean;

@Builder
public record QuizResultDetailResponse(
        QuizResultInfo quiz_result,
        List<AnswerInfo> answers
) {
    @Builder
    public record QuizResultInfo(
            Long quiz_id,
            String group_name,
            Integer score,
            Integer correct_count,
            Integer total_questions,
            LocalDateTime created_at
    ) {}

    @Builder
    public record AnswerInfo(
            Long quiz_result_id,
            Long question_id,
            Integer question_number,
            String question_text,
            String type,
            String explanation,
            List<QuizDetailResponse.OptionInfo> options,
            String user_answer,
            String correct_answer,
            Boolean is_correct
    ) {}

    public static QuizResultDetailResponse from(
            QuizResult result,
            List<QuizQuestion> allQuestions,
            List<QuizUserAnswer> answers
    ) {
        Map<Long, QuizUserAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a, (a, b) -> a));

        Set<Long> seen = new HashSet<>();

        AtomicInteger number = new AtomicInteger(1);

        List<AnswerInfo> answerInfos = allQuestions.stream()
                .filter(q -> seen.add(q.getId()))
                .map(q -> {
                    QuizUserAnswer a = answerMap.get(q.getId());
                    boolean answered = (a != null);

                    return AnswerInfo.builder()
                            .quiz_result_id(result.getId())
                            .question_id(q.getId())
                            .question_number(number.getAndIncrement())
                            .question_text(q.getQuestionText())
                            .type(convertTypeToKorean(q.getType()))
                            .explanation(q.getExplanation())
                            .options(q.getOptions() == null ? List.of() :
                                    q.getOptions().stream()
                                            .map(QuizDetailResponse.OptionInfo::fromEntity)
                                            .collect(Collectors.toList()))
                            .user_answer(answered ? a.getUserAnswer() : null)
                            .correct_answer(q.getCorrectAnswer())
                            .is_correct(answered && Boolean.TRUE.equals(a.getIsCorrect()))
                            .build();
                })
                .toList();

        return QuizResultDetailResponse.builder()
                .quiz_result(QuizResultInfo.builder()
                        .quiz_id(result.getQuiz().getId())
                        .group_name(result.getQuiz().getGroup().getName())
                        .score(result.getScore())
                        .correct_count(result.getCorrectCount())
                        .total_questions(result.getQuiz().getTotalQuestions())
                        .created_at(result.getCreatedAt())
                        .build())
                .answers(answerInfos)
                .build();
    }
}
