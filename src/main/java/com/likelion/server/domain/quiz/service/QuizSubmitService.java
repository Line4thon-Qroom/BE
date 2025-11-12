package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.quiz.entity.*;
import com.likelion.server.domain.quiz.exception.*;
import com.likelion.server.domain.quiz.repository.*;
import com.likelion.server.domain.quiz.web.dto.QuizSubmitRequest;
import com.likelion.server.domain.quiz.web.dto.QuizSubmitResponse;
import com.likelion.server.domain.ranking.entity.GroupRanking;
import com.likelion.server.domain.ranking.repository.GroupRankingRepository;
import com.likelion.server.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizSubmitService {

    private final QuizResultRepository quizResultRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizUserAnswerRepository quizUserAnswerRepository;
    private final GroupRankingRepository groupRankingRepository;
    private final EntityManager em;

    public QuizSubmitResponse submitQuiz(Long userId, QuizSubmitRequest request) {

        // 요청값 검증
        if (request.quiz_result_id() == null)
            throw new QuizInvalidRequestException();

        // 퀴즈 결과 존재 여부 확인
        QuizResult result = quizResultRepository.findById(request.quiz_result_id())
                .orElseThrow(QuizResultNotFoundException::new);

        Quiz quiz = result.getQuiz();
        if (quiz == null)
            throw new QuizNotFoundException();

        // 퀴즈 문항 존재 확인
        List<QuizQuestion> questions = quizQuestionRepository.findAllByQuizId(quiz.getId());
        if (questions.isEmpty())
            throw new QuizInvalidFormatException();

        User user = em.getReference(User.class, userId);

        // 문제 번호 → 문제 엔티티 매핑
        Map<Integer, QuizQuestion> questionMap = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            questionMap.put(i + 1, questions.get(i));
        }

        int totalQuestions = questions.size();
        int correctCount = 0;

        // 제출된 답안 채점
        for (QuizSubmitRequest.Answer ans : request.answers()) {

            // 문제 번호 유효성 체크
            QuizQuestion question = questionMap.get(ans.question_number());
            if (question == null)
                throw new QuizInvalidQuestionNumberException();

            // 중복 제출 방지
            if (quizUserAnswerRepository.existsByQuizResultIdAndQuestionId(result.getId(), question.getId()))
                throw new QuizDuplicateAnswerException();

            // 채점 수행
            boolean isCorrect = checkAnswer(question, ans.user_answer());
            QuizUserAnswer userAnswer = ans.toEntity(result, question, user, isCorrect);

            try {
                quizUserAnswerRepository.save(userAnswer);
            } catch (Exception e) {
                throw new QuizSaveFailException(); // DB 저장 실패 예외
            }

            if (isCorrect) correctCount++;
        }

        // 점수 계산 및 결과 업데이트
        int score = (int) Math.round((correctCount * 100.0) / totalQuestions);
        result.setScore(score);
        result.setCorrectCount(correctCount);

        try {
            quizResultRepository.save(result);
        } catch (Exception e) {
            throw new QuizSaveFailException();
        }

        // 그룹 정보 확인
        Group group = quiz.getGroup();
        if (group == null)
            throw new QuizGroupNotFoundException();

        // 그룹 랭킹 업데이트
        updateGroupRank(user, group);

        // 최종 응답 반환
        return QuizSubmitResponse.fromEntity(result.getId(), score, correctCount, totalQuestions);
    }

    /**
     * 문제 정답 채점 로직
     */
    private boolean checkAnswer(QuizQuestion question, String userAnswer) {
        if (userAnswer == null || question.getCorrectAnswer() == null) return false;

        String correct = question.getCorrectAnswer().trim();
        String user = userAnswer.trim();

        return switch (question.getType()) {
            case OX -> user.equalsIgnoreCase(correct);
            case MULTIPLE_CHOICE -> user.equalsIgnoreCase(correct)
                    || correct.contains(user)
                    || user.contains(correct);
            case SHORT_ANSWER -> correct.replaceAll("[^가-힣a-zA-Z0-9]", "")
                    .equalsIgnoreCase(user.replaceAll("[^가-힣a-zA-Z0-9]", ""));
            default -> false;
        };
    }

    /**
     * 그룹 랭킹 업데이트
     */
    private void updateGroupRank(User user, Group group) {
        List<QuizResult> allResultsInGroup = quizResultRepository.findAllByUserAndGroup(user, group);

        int totalWeightedScore = allResultsInGroup.stream()
                .mapToInt(result -> {
                    Quiz quiz = result.getQuiz();
                    if (quiz == null)
                        throw new QuizNotFoundException();

                    int correctCount = result.getCorrectCount();

                    return switch (quiz.getDifficulty()) {
                        case 상 -> correctCount * 3;
                        case 중 -> correctCount * 2;
                        case 하 -> correctCount * 1;
                    };
                })
                .sum();

        GroupRanking ranking = groupRankingRepository.findByGroupAndUser(group, user)
                .orElse(GroupRanking.builder()
                        .group(group)
                        .user(user)
                        .build());

        ranking.setTotalScore(totalWeightedScore);

        try {
            groupRankingRepository.save(ranking);
        } catch (Exception e) {
            throw new QuizSaveFailException();
        }

        recalculateRankPositions(group);
    }

    /**
     * 그룹 내 등수 재계산
     */
    private void recalculateRankPositions(Group group) {
        List<GroupRanking> rankings = groupRankingRepository.findAllByGroupOrderByTotalScoreDesc(group);

        int currentRank = 1;
        int sameScoreCount = 0;
        Integer previousScore = null;

        for (GroupRanking rank : rankings) {
            if (previousScore == null || !rank.getTotalScore().equals(previousScore)) {
                currentRank += sameScoreCount;
                sameScoreCount = 0;
            }

            rank.setRankPosition(currentRank);
            sameScoreCount++;
            previousScore = rank.getTotalScore();
        }
    }
}
