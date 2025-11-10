package com.likelion.server.domain.quiz.repository;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    int countByPdfId(Long pdfId);

    List<Quiz> findAllByGroup(Group group);
}
