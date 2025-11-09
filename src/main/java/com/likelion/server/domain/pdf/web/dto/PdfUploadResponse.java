package com.likelion.server.domain.pdf.web.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record PdfUploadResponse(
        Long id,
        Long groupId,
        String fileName,
        String s3Url,
        Uploader uploader,
        LocalDateTime createdAt
) {
    @Builder
    public record Uploader(Long id, String name) {}

}
