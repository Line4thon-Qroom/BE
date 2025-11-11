package com.likelion.server.domain.quiz.repository;

import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.quiz.entity.QuizUserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizUserAnswerRepository extends JpaRepository<QuizUserAnswer, Long> {

    @Query("SELECT DISTINCT a FROM QuizUserAnswer a WHERE a.quizResult.id = :quizResultId")
    List<QuizUserAnswer> findAllByQuizResultId(@Param("quizResultId") Long quizResultId);

    boolean existsByQuizResultIdAndQuestionId(Long quizResultId, Long questionId);

    List<QuizUserAnswer> findAllByQuizResultAndIsCorrect(QuizResult quizResult, Boolean isCorrect);

    Optional<QuizUserAnswer> findByQuizResultIdAndQuestionId(Long quizResultId, Long questionId);

    void deleteAllByQuizResultIn(List<QuizResult> quizResults);
}
