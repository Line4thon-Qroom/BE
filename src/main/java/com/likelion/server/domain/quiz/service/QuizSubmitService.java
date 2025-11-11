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

import java.util.List;

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

        if (request.quiz_result_id() == null || request.quiz_id() == null)
            throw new QuizInvalidRequestException();

        QuizResult result = quizResultRepository.findById(request.quiz_result_id())
                .orElseThrow(QuizResultNotFoundException::new);

        List<QuizQuestion> questions = quizQuestionRepository.findAllByQuizId(request.quiz_id());
        if (questions.isEmpty()) throw new QuizNotFoundException();

        User user = em.getReference(User.class, userId);

        int totalQuestions = questions.size();
        int correctCount = 0;

        for (QuizSubmitRequest.Answer ans : request.answers()) {
            QuizQuestion question = em.getReference(QuizQuestion.class, ans.question_id());

            // 중복 제출 방지
            if (quizUserAnswerRepository.existsByQuizResultIdAndQuestionId(result.getId(), question.getId()))
                throw new QuizDuplicateAnswerException();

            boolean isCorrect = checkAnswer(question, ans.user_answer());
            QuizUserAnswer userAnswer = ans.toEntity(result, question, user, isCorrect);
            quizUserAnswerRepository.save(userAnswer);

            if (isCorrect) correctCount++;
        }

        int score = (int) Math.round((correctCount * 100.0) / totalQuestions);
        result.setScore(score);
        result.setCorrectCount(correctCount);
        quizResultRepository.save(result);

        updateGroupRank(user, result.getQuiz().getGroup());

        return QuizSubmitResponse.fromEntity(result.getId(), score, correctCount, totalQuestions);
    }

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

    private void updateGroupRank(User user, Group group) {
        // 그룹에서 유저가 푼 모든 퀴즈 결과(QuizResult) 조회
        List<QuizResult> allResultsInGroup = quizResultRepository.findAllByUserAndGroup(user, group);

        // 난이도 가중치 점수 합산
        int totalWeightedScore = allResultsInGroup.stream()
                .mapToInt(result -> {
                    Quiz quiz = result.getQuiz(); // 결과에서 퀴즈 정보를 가져옴
                    int correctCount = result.getCorrectCount();

                    // 퀴즈 난이도에 따라 가중치 부여
                    return switch (quiz.getDifficulty()) {
                        case 상 -> correctCount * 3; // 상 3점
                        case 중 -> correctCount * 2; // 중 2점
                        case 하 -> correctCount * 1; // 하 1점
                    };
                })
                .sum();

        // GroupRanking 테이블에서 기존 랭킹 정보를 찾거나 새로 생성
        GroupRanking ranking = groupRankingRepository.findByGroupAndUser(group, user)
                .orElse(GroupRanking.builder()
                        .group(group)
                        .user(user)
                        .build());

        // 갱신된 가중치 점수를 랭킹 테이블에 저장
        ranking.setTotalScore(totalWeightedScore);
        groupRankingRepository.save(ranking);

        // 등수 재계산
        recalculateRankPositions(group);
    }

    private void recalculateRankPositions(Group group) {
        // 해당 그룹의 모든 랭킹을 총 점수(totalScore)가 높은 순으로 조회
        List<GroupRanking> rankings = groupRankingRepository.findAllByGroupOrderByTotalScoreDesc(group);

        int currentRank = 1;
        int sameScoreCount = 0;
        Integer previousScore = null;

        // 랭킹을 순회하며 등수(rankPosition) 부여
        for (GroupRanking rank : rankings) {
            if (previousScore == null || !rank.getTotalScore().equals(previousScore)) {
                // 이전 점수와 다르면, 현재 등수를 (동점자 수 + 1)만큼 증가시킴
                currentRank += sameScoreCount;
                sameScoreCount = 0; // 동점자 수 초기화
            }

            rank.setRankPosition(currentRank);
            sameScoreCount++;
            previousScore = rank.getTotalScore();
        }
    }
}
