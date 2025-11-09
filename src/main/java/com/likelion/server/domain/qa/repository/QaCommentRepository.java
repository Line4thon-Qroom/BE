package com.likelion.server.domain.qa.repository;

import com.likelion.server.domain.qa.entity.QaComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QaCommentRepository extends JpaRepository<QaComment, Long> {}
