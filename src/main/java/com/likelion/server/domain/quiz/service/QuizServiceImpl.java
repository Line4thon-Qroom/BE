package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.pdf.entity.Pdf;
import com.likelion.server.domain.pdf.repository.PdfRepository;
import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.repository.QaBoardRepository;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.quiz.entity.enums.Difficulty;
import com.likelion.server.domain.quiz.entity.enums.Type;
import com.likelion.server.domain.quiz.repository.QuizRepository;
import com.likelion.server.domain.quiz.repository.QuizQuestionRepository;
import com.likelion.server.domain.quiz.web.dto.CreateQuizRequest;
import com.likelion.server.domain.quiz.web.dto.CreateQuizResponse;
import com.likelion.server.domain.quiz.web.dto.QuizDetailResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.likelion.server.domain.quiz.entity.enums.Type.*;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final PdfRepository pdfRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QaBoardRepository qaBoardRepository;
    private final AiQuizGenerator aiQuizGenerator;
    private final EntityManager em;

    // 세부 로직 분리 메서드
    private Pdf validateAndGetPdf(Long pdfId) {
        return pdfRepository.findById(pdfId)
                .orElseThrow(() -> new IllegalArgumentException("해당 PDF를 찾을 수 없습니다."));
    }

    private Quiz createQuizEntity(Pdf pdf, CreateQuizRequest request, int round) {
        Difficulty difficulty = Difficulty.fromKorean(request.difficulty());
        return Quiz.builder()
                .pdf(pdf)
                .round(round)
                .difficulty(difficulty)
                .questionTypes(String.join(",", request.question_types()))
                .totalQuestions(request.total_questions())
                .title(pdf.getFileName())
                .build();
    }

    private QaBoard createQaBoard(Pdf pdf, Quiz quiz) {
        return QaBoard.builder()
                .quiz(quiz)
                .boardName(pdf.getFileName() + " Quiz 게시판")
                .group(pdf.getGroup())
                .build();
    }


    // 1. 퀴즈 생성
    @Override
    public CreateQuizResponse createQuiz(Long userId, CreateQuizRequest request) {

        // PDF 검증 및 조회
        Pdf pdf = validateAndGetPdf(request.pdf_id());

        // 라운드 계산
        int round = quizRepository.countByPdfId(pdf.getId()) + 1;

        // 퀴즈 생성 및 저장
        Quiz quiz = createQuizEntity(pdf, request, round);
        quizRepository.saveAndFlush(quiz);

        // AI 퀴즈 생성
        List<QuizQuestion> generatedQuestions = aiQuizGenerator.generateQuestions(pdf, quiz, request);

        // 문제 저장 (Cascade 안 쓰는 경우 명시적으로 saveAll)
        quizQuestionRepository.saveAll(generatedQuestions);

        // QA 게시판 생성
        QaBoard qaBoard = createQaBoard(pdf, quiz);
        qaBoardRepository.save(qaBoard);

        // 응답 DTO 변환
        return CreateQuizResponse.fromEntity(quiz, qaBoard);
    }


    // 2. 퀴즈 조회
    @Override
    public QuizDetailResponse getQuizDetail(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈를 찾을 수 없습니다."));
        List<QuizQuestion> questions = quizQuestionRepository.findAllByQuizId(quizId);

        return QuizDetailResponse.fromEntities(quiz, questions);
    }


}
