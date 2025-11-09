package com.likelion.server.domain.qa.repository;

import com.likelion.server.domain.qa.entity.QaPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QaPostRepository extends JpaRepository<QaPost, Long> {}
