package com.likelion.server.domain.pdf.repository;

import com.likelion.server.domain.pdf.entity.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfRepository extends JpaRepository<Pdf, Long> {}
