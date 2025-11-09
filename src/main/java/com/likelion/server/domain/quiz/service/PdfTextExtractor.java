package com.likelion.server.domain.quiz.service;
import com.likelion.server.domain.pdf.entity.Pdf;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;

@Component
@RequiredArgsConstructor
public class PdfTextExtractor {

    private final S3Client s3Client;
    @Value("${bucket-name}")
    private String bucket;

    public String extract(Pdf pdf) {

        try {
            String key = extractS3Key(pdf.getS3Url());
            // [1] S3 다운로드 정보 출력
            System.out.println("[DEBUG] 📄 PDF S3 URL: " + pdf.getS3Url());
            System.out.println("[DEBUG] 📦 Extracted Key: " + key);


            ResponseInputStream<?> stream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());

            File tempFile = File.createTempFile("quiz_", ".pdf");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                stream.transferTo(fos);
            }
            // [2] 파일 다운로드 완료 후 파일 크기 확인
            System.out.println("[DEBUG] 📁 Downloaded temp file: " + tempFile.getAbsolutePath());
            System.out.println("[DEBUG] 📏 File size: " + tempFile.length() + " bytes");

            // [3] 빈 파일이면 바로 예외 발생시키기
            if (tempFile.length() == 0) {
                throw new RuntimeException("S3에서 받은 PDF 파일이 비어 있습니다. 업로드 또는 Key를 확인하세요.");
            }

            // [4] PDF 텍스트 추출
            try (PDDocument doc = PDDocument.load(tempFile)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(doc);

                System.out.println("[DEBUG] ✅ PDF 텍스트 추출 완료, 길이: " + text.length());

                return text.length() > 8000 ? text.substring(0, 8000) : text;
            } finally {
                tempFile.delete();
            }

        } catch (Exception e) { // 콘솔에 자세한 원인 출력
            throw new RuntimeException("PDF 텍스트 추출 실패", e);
        }
    }

    private String extractS3Key(String s3Url) {
        int idx = s3Url.indexOf(".amazonaws.com/") + ".amazonaws.com/".length();
        return s3Url.substring(idx);
    }
}
