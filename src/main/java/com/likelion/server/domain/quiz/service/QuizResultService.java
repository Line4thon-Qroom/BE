package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.quiz.entity.QuizUserAnswer;
import com.likelion.server.domain.quiz.repository.QuizQuestionRepository;
import com.likelion.server.domain.quiz.repository.QuizRepository;
import com.likelion.server.domain.quiz.repository.QuizResultRepository;
import com.likelion.server.domain.quiz.repository.QuizUserAnswerRepository;
import com.likelion.server.domain.quiz.web.dto.QuizResultDetailResponse;
import com.likelion.server.domain.quiz.web.dto.QuizResultRequest;
import com.likelion.server.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;
    private final QuizUserAnswerRepository quizUserAnswerRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizRepository quizRepository;
    private final EntityManager em;

    public QuizResult startQuiz(Long userId, QuizResultRequest request) {

        Quiz quiz = quizRepository.findById(request.quiz_id())
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈를 찾을 수 없습니다."));

        User user = em.getReference(User.class, userId);
        Group group = quiz.getGroup();

        QuizResult quizResult = request.toEntity(quiz, user, group);

        return quizResultRepository.save(quizResult);
    }

    public QuizResultDetailResponse getQuizResultDetail(Long userId, Long quizResultId) {

        QuizResult result = quizResultRepository.findById(quizResultId)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 결과를 찾을 수 없습니다."));

        if (!result.getUser().getId().equals(userId)) {
            throw new SecurityException("본인의 결과만 조회할 수 있습니다.");
        }

        // 사용자 답변 목록 조회
        List<QuizUserAnswer> answers = quizUserAnswerRepository.findAllByQuizResultId(quizResultId);
        List<QuizQuestion> allQuestions = quizQuestionRepository.findAllByQuizId(result.getQuiz().getId());
        return QuizResultDetailResponse.from(result, allQuestions, answers);

    }
}
