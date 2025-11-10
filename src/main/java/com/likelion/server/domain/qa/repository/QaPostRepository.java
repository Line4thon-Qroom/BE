package com.likelion.server.domain.qa.repository;

import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.entity.QaPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QaPostRepository extends JpaRepository<QaPost, Long> {
    // 특정 게시판에 속한 게시글 목록 조회
    List<QaPost> findAllByBoard(QaBoard board);
}
