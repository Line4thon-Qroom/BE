package com.likelion.server.domain.qa.repository;

import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.entity.QaPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QaPostRepository extends JpaRepository<QaPost, Long> {

    List<QaPost> findAllByBoardIn(List<QaBoard> boardsInGroup);

    List<QaPost> findAllByBoardOrderByCreatedAtAsc(QaBoard board);
}
