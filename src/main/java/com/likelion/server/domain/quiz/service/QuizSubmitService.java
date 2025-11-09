package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.quiz.entity.*;
import com.likelion.server.domain.quiz.repository.*;
import com.likelion.server.domain.quiz.web.dto.QuizSubmitRequest;
import com.likelion.server.domain.quiz.web.dto.QuizSubmitResponse;
import com.likelion.server.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.Builder;
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

        QuizResult result = quizResultRepository.findById(request.quiz_result_id())
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 결과를 찾을 수 없습니다."));
        User user = em.getReference(User.class, userId);

        List<QuizQuestion> questions = quizQuestionRepository.findAllByQuizId(request.quiz_id());
        int totalQuestions = questions.size();
        int correctCount = 0;

        for (QuizSubmitRequest.Answer ans : request.answers()) {
            QuizQuestion question = em.getReference(QuizQuestion.class, ans.question_id());
            boolean isCorrect = checkAnswer(ans.user_answer(), question.getCorrectAnswer());

            QuizUserAnswer userAnswer = ans.toEntity(result, question, user, isCorrect);
            quizUserAnswerRepository.save(userAnswer);

            if (isCorrect) correctCount++;
        }

        int score = (int) Math.round((correctCount * 100.0) / totalQuestions);

        // update result
        result = result.toBuilder()
                .score(score)
                .correctCount(correctCount)
                .build();

        quizResultRepository.save(result);

        return QuizSubmitResponse.fromEntity(result.getId(), score, correctCount, totalQuestions);
    }

    private boolean checkAnswer(String userAnswer, String correctAnswer) {
        if (userAnswer == null || correctAnswer == null) return false;
        return userAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
    }
}
