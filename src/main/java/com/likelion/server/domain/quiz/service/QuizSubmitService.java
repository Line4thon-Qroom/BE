package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.quiz.entity.*;
import com.likelion.server.domain.quiz.exception.*;
import com.likelion.server.domain.quiz.repository.*;
import com.likelion.server.domain.quiz.web.dto.QuizSubmitRequest;
import com.likelion.server.domain.quiz.web.dto.QuizSubmitResponse;
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
}
