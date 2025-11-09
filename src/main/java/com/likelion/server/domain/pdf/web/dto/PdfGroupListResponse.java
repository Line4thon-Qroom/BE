package com.likelion.server.domain.pdf.web.dto;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PdfGroupListResponse(
        Long groupId,
        String groupName,
        List<PdfInfo> pdfList
) {
    @Builder
    public record PdfInfo(
            Long id,
            String title,
            String fileUrl,
            Uploader uploader,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    @Builder
    public record Uploader(
            Long id,
            String nickname
    ) {}
}
