package com.likelion.server.domain.pdf.service;

import com.likelion.server.domain.pdf.web.dto.PdfDeleteResponse;
import com.likelion.server.domain.pdf.web.dto.PdfGroupListResponse;
import com.likelion.server.domain.pdf.web.dto.PdfUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PdfService {

    PdfUploadResponse upload(Long userId, Long groupId, MultipartFile file);

    PdfDeleteResponse delete(Long pdfId);

    PdfGroupListResponse getPdfListByGroup(Long groupId);
}
