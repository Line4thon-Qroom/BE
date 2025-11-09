package com.likelion.server.domain.quiz.repository;

import com.likelion.server.domain.quiz.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    // ✅ 퀴즈 ID 기준으로 문제 조회
    List<QuizQuestion> findAllByQuizId(Long quizId);
}
