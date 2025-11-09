package com.likelion.server.domain.pdf.web.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PdfDeleteResponse(
        Long id,
        String fileName,
        String s3Url,
        Uploader uploader,
        LocalDateTime deletedAt
) {
    @Builder
    public record Uploader(Long id, String name) {}

}
