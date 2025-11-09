package com.likelion.server.domain.quiz.repository;

import com.likelion.server.domain.quiz.entity.QuizUserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizUserAnswerRepository extends JpaRepository<QuizUserAnswer, Long> {
}
