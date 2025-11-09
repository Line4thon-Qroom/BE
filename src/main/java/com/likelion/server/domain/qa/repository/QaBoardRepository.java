package com.likelion.server.domain.qa.repository;

import com.likelion.server.domain.qa.entity.QaBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QaBoardRepository extends JpaRepository<QaBoard, Long> {}
