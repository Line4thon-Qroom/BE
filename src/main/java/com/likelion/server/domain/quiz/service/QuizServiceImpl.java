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

    @Override
    public CreateQuizResponse createQuiz(Long userId, CreateQuizRequest request) {

        // PDF 검증
        Pdf pdf = pdfRepository.findById(request.pdf_id())
                .orElseThrow(() -> new IllegalArgumentException("해당 PDF를 찾을 수 없습니다."));

        // 라운드 계산
        int round = quizRepository.countByPdfId(pdf.getId()) + 1;

        // String → Difficulty enum 변환
        Difficulty difficulty = switch (request.difficulty()) {
            case "상" -> Difficulty.상;
            case "중" -> Difficulty.중;
            case "하" -> Difficulty.하;
            default -> throw new IllegalArgumentException("잘못된 난이도 값입니다: " + request.difficulty());
        };

        // Quiz 생성
        Quiz quiz = Quiz.builder()
                .pdf(pdf)
                .round(round)
                .difficulty(difficulty)
                .questionTypes(String.join(",", request.question_types()))
                .totalQuestions(request.total_questions())
                .title(pdf.getFileName())
                .build();
        Quiz savedQuiz = quizRepository.saveAndFlush(quiz);

        // AI 호출 (동기)
        List<QuizQuestion> generatedQuestions = aiQuizGenerator.generateQuestions(pdf, quiz, request);

        // 생성된 문제 저장
        quizQuestionRepository.saveAll(generatedQuestions);

        // QA 게시판 생성
        QaBoard qaBoard = QaBoard.builder()
                .quiz(quiz)
                .boardName(pdf.getFileName())
                .group(pdf.getGroup())
                .build();
        qaBoardRepository.save(qaBoard);

        // 응답 생성
        return CreateQuizResponse.builder()
                .id(quiz.getId())
                .pdf_id(pdf.getId())
                .round(round)
                .difficulty(Difficulty.valueOf(quiz.getDifficulty().name()))
                .question_types(request.question_types())
                .total_questions(quiz.getTotalQuestions())
                .qa_board(new CreateQuizResponse.QaBoard(
                        qaBoard.getId(),
                        qaBoard.getBoardName()
                ))
                .build();
    }


    // 퀴즈 조회
    @Override
    public QuizDetailResponse getQuizDetail(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈를 찾을 수 없습니다."));

        List<QuizQuestion> questions = quizQuestionRepository.findAllByQuizId(quizId);

        return QuizDetailResponse.builder()
                .quiz(new QuizDetailResponse.QuizInfo(
                        quiz.getId(),
                        quiz.getPdf().getId(),
                        quiz.getPdf().getFileName(),
                        quiz.getDifficulty().name(),
                        quiz.getRound(),
                        quiz.getTotalQuestions(),
                        quiz.getCreatedAt()
                ))
                .questions(
                        questions.stream()
                                .map(q -> new QuizDetailResponse.QuestionInfo(
                                        q.getId(),
                                        convertTypeToKorean(q.getType()),
                                        q.getQuestionText(),
                                        q.getCorrectAnswer(),
                                        q.getExplanation()
                                ))
                                .toList()
                )
                .build();
    }

    private String convertTypeToKorean(Type type) {
        return switch (type) {
            case OX -> "OX";
            case MULTIPLE_CHOICE -> "객관식";
            case SHORT_ANSWER -> "단답형";
        };
    }


}
