package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.quiz.repository.QuizRepository;
import com.likelion.server.domain.quiz.repository.QuizResultRepository;
import com.likelion.server.domain.quiz.web.dto.QuizResultRequest;
import com.likelion.server.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;
    private final QuizRepository quizRepository;
    private final EntityManager em;

    public QuizResult startQuiz(Long userId, QuizResultRequest request) {

        Quiz quiz = quizRepository.findById(request.quiz_id())
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈를 찾을 수 없습니다."));

        User user = em.getReference(User.class, userId);
        Group group = quiz.getPdf().getGroup();

        QuizResult quizResult = request.toEntity(quiz, user, group);

        return quizResultRepository.save(quizResult);
    }
}
