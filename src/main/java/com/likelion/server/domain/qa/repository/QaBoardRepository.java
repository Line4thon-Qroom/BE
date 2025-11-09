package com.likelion.server.domain.qa.repository;

import com.likelion.server.domain.qa.entity.QaBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QaBoardRepository extends JpaRepository<QaBoard, Long> {

    // 특정 퀴즈에 연결된 QA 게시판 조회
    Optional<QaBoard> findByQuizId(Long quizId);
}
