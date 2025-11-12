package com.likelion.server.domain.quiz.service;
import com.likelion.server.domain.pdf.entity.Pdf;
import com.likelion.server.domain.quiz.exception.QuizGenerationFailException;
import com.likelion.server.domain.quiz.exception.QuizInvalidFormatException;
import com.likelion.server.domain.quiz.exception.QuizNotFoundException;
import com.likelion.server.domain.quiz.exception.QuizPdfInvalidException;
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
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PdfTextExtractor {

    private final S3Client s3Client;
    @Value("${bucket-name}")
    private String bucket;

    public String extract(Pdf pdf) {

        try {
            if (pdf == null || pdf.getS3Url() == null)
                throw new QuizNotFoundException(); // PDF 객체 자체가 잘못된 경우


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
                throw new QuizPdfInvalidException(); // 비어있는 파일
            }

            // [4] PDF 텍스트 추출
            try (PDDocument doc = PDDocument.load(tempFile)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(doc);

                System.out.println("[DEBUG] ✅ PDF 텍스트 추출 완료, 길이: " + text.length());

                if (text == null || text.isBlank())
                    throw new QuizPdfInvalidException(); // 내용 없음

                return text.length() > 8000 ? text.substring(0, 8000) : text;
            } catch (IOException e) {
                throw new QuizPdfInvalidException(); // 손상된 PDF
            } finally {
                tempFile.delete();
            }

        } catch (QuizNotFoundException | QuizPdfInvalidException | QuizGenerationFailException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new QuizGenerationFailException();
        }
    }

    private String extractS3Key(String s3Url) {
        try {
            int idx = s3Url.indexOf(".amazonaws.com/") + ".amazonaws.com/".length();
            return s3Url.substring(idx);
        } catch (Exception e) {
            throw new QuizPdfInvalidException();
        }
    }
}
