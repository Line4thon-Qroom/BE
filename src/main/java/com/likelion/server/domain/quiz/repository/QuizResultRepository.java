package com.likelion.server.domain.quiz.repository;

import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    Optional<QuizResult> findByUserAndQuiz(User user, Quiz quiz);

    List<QuizResult> findAllByUser(User user);

    Optional<QuizResult> findByUserAndQuizId(User user, Long quizId);
}
