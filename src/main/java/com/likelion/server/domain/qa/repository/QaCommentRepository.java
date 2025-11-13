package com.likelion.server.domain.qa.repository;

import com.likelion.server.domain.qa.entity.QaComment;
import com.likelion.server.domain.qa.entity.QaPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QaCommentRepository extends JpaRepository<QaComment, Long> {
    // 특정 게시글의 댓글 수 카운트
    Integer countByPost(QaPost post);

    void deleteAllByPostIn(List<QaPost> allPosts);

    List<QaComment> findAllByPostOrderByCreatedAtAsc(QaPost post);
}
