package com.likelion.server.domain.pdf.web.controller;

import com.likelion.server.domain.pdf.service.PdfServiceImpl;
import com.likelion.server.domain.pdf.web.dto.PdfDeleteResponse;
import com.likelion.server.domain.pdf.web.dto.PdfGroupListResponse;
import com.likelion.server.domain.pdf.web.dto.PdfUploadResponse;
import com.likelion.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf")
public class PdfController {

    private final PdfServiceImpl pdfServiceImpl;

    // PDF 업로드
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SuccessResponse<PdfUploadResponse> uploadPdf(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam("group_id") Long groupId,
            @RequestPart("file") MultipartFile file
    ) {
        PdfUploadResponse data = pdfServiceImpl.upload(userId, groupId, file);
        return SuccessResponse.created(data);
    }

    // PDF 삭제
    @DeleteMapping("/{pdf_id}")
    public SuccessResponse<PdfDeleteResponse> deletePdf(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("pdf_id") Long pdfId
    ) {
        PdfDeleteResponse data = pdfServiceImpl.delete(pdfId, userId);
        return SuccessResponse.ok(data);
    }

    //그룹별 pdf 조회
    @GetMapping()
    public SuccessResponse<PdfGroupListResponse> getGroupPdfList(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam("group_id") Long groupId
    ) {
        PdfGroupListResponse data = pdfServiceImpl.getPdfListByGroup(groupId);
        return SuccessResponse.ok(data);
    }
}
