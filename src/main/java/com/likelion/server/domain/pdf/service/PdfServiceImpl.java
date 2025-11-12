package com.likelion.server.domain.pdf.service;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.pdf.entity.Pdf;
import com.likelion.server.domain.pdf.repository.PdfRepository;
import com.likelion.server.domain.pdf.web.dto.PdfDeleteResponse;
import com.likelion.server.domain.pdf.web.dto.PdfGroupListResponse;
import com.likelion.server.domain.pdf.web.dto.PdfUploadResponse;
import com.likelion.server.domain.quiz.exception.QuizGenerationFailException;
import com.likelion.server.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PdfServiceImpl implements PdfService {

    private final PdfRepository pdfRepository;
    private final S3Client s3Client;
    private final EntityManager em;

    @Value("${bucket-name}")
    private String bucket;

    // PDF 업로드
    @Override
    public PdfUploadResponse upload(Long userId, Long groupId, MultipartFile file) {

        // (1) 파일 유효성 검사
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String originalName = file.getOriginalFilename();
        String contentType = file.getContentType();

        // (2) 확장자 검사
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("PDF 파일만 업로드할 수 있습니다. (확장자 오류)");
        }

        // (3) MIME 타입 검사
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("유효한 PDF 파일이 아닙니다. (MIME 타입 오류)");
        }

        // (4) 파일 키 생성
        String fileKey = String.format("%d/%s", groupId, originalName);

        try {
            // S3 업로드 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .build();

            // 실제 업로드 수행
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (IOException e) {
            e.printStackTrace();
            throw new QuizGenerationFailException();
        }

        // S3 퍼블릭 URL 생성
        String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucket, fileKey);

        // DB에 PDF 정보 저장
        Pdf pdf = Pdf.builder()
                .group(em.getReference(Group.class, groupId))
                .uploader(em.getReference(User.class, userId))
                .fileName(originalName)
                .s3Url(fileUrl)
                .build();

        Pdf saved = pdfRepository.save(pdf);
        User uploader = em.find(User.class, userId);

        return PdfUploadResponse.builder()
                .id(saved.getId())
                .groupId(groupId)
                .fileName(saved.getFileName())
                .s3Url(saved.getS3Url())
                .uploader(new PdfUploadResponse.Uploader(uploader.getId(), uploader.getNickname()))
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // PDF 삭제
    @Override
    public PdfDeleteResponse delete(Long pdfId) {
        Pdf pdf = pdfRepository.findById(pdfId)
                .orElseThrow(() -> new IllegalArgumentException("해당 PDF를 찾을 수 없습니다."));

        // S3 키 추출 (버킷 내 경로)
        String key = extractS3Key(pdf.getS3Url());

        // S3에서 삭제
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);

        // DB에서 삭제
        pdfRepository.delete(pdf);

        User uploader = pdf.getUploader();

        return PdfDeleteResponse.builder()
                .id(pdf.getId())
                .fileName(pdf.getFileName())
                .s3Url(pdf.getS3Url())
                .uploader(new PdfDeleteResponse.Uploader(uploader.getId(), uploader.getNickname()))
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // URL에서 S3 key 부분 추출
    private String extractS3Key(String s3Url) {
        int idx = s3Url.indexOf(bucket) + bucket.length() + 1;
        return s3Url.substring(idx);
    }

    // 그룹별 pdf 조회
    @Override
    public PdfGroupListResponse getPdfListByGroup(Long groupId) {

        // 그룹 확인
        Group group = em.find(Group.class, groupId);
        if (group == null) {
            throw new IllegalArgumentException("해당 그룹을 찾을 수 없습니다.");
        }

        // PDF 목록 조회
        List<Pdf> pdfList = pdfRepository.findAllByGroupId(groupId);

        // DTO 변환
        List<PdfGroupListResponse.PdfInfo> pdfInfos = pdfList.stream()
                .map(pdf -> PdfGroupListResponse.PdfInfo.builder()
                        .id(pdf.getId())
                        .title(pdf.getFileName())
                        .fileUrl(pdf.getS3Url())
                        .uploader(new PdfGroupListResponse.Uploader(
                                pdf.getUploader().getId(),
                                pdf.getUploader().getNickname()
                        ))
                        .createdAt(pdf.getCreatedAt())
                        .updatedAt(pdf.getUpdatedAt())
                        .build())
                .toList();

        return PdfGroupListResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .pdfList(pdfInfos)
                .build();
    }
}
