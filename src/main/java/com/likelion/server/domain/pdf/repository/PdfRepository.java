package com.likelion.server.domain.pdf.repository;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.pdf.entity.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PdfRepository extends JpaRepository<Pdf, Long> {
    List<Pdf> findAllByGroupId(Long groupId);
}
