package com.likelion.server.domain.quiz.repository;

import com.likelion.server.domain.quiz.entity.QuizOption;
import com.likelion.server.domain.quiz.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, Long> {
    // 질문 ID 기준 보기 조회
    List<QuizOption> findAllByQuestion(QuizQuestion question);

    void deleteAllByQuestion(QuizQuestion question);
}
