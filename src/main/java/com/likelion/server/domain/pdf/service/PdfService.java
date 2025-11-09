package com.likelion.server.domain.pdf.service;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.pdf.entity.Pdf;
import com.likelion.server.domain.pdf.repository.PdfRepository;
import com.likelion.server.domain.pdf.web.dto.PdfDeleteResponse;
import com.likelion.server.domain.pdf.web.dto.PdfUploadResponse;
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

@Service
@RequiredArgsConstructor
@Transactional
public class PdfService {

    private final PdfRepository pdfRepository;
    private final S3Client s3Client;
    private final EntityManager em;

    @Value("${bucket-name}")
    private String bucket;

    // PDF 업로드
    public PdfUploadResponse upload(Long userId, Long groupId, MultipartFile file) {
        String originalName = file.getOriginalFilename();
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
            throw new RuntimeException("S3 업로드 실패", e);
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
    public PdfDeleteResponse delete(Long pdfId, Long userId) {
        Pdf pdf = pdfRepository.findById(pdfId)
                .orElseThrow(() -> new IllegalArgumentException("해당 PDF를 찾을 수 없습니다."));

        // 업로더 본인 확인
        if (!pdf.getUploader().getId().equals(userId)) {
            throw new SecurityException("본인이 업로드한 파일만 삭제할 수 있습니다.");
        }

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


        User uploader = em.find(User.class, userId);

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
}
