package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.quiz.web.dto.CreateQuizRequest;
import com.likelion.server.domain.quiz.web.dto.CreateQuizResponse;
import com.likelion.server.domain.quiz.web.dto.QuizDetailResponse;
import com.likelion.server.domain.quiz.web.dto.QuizQaResponse;
import org.springframework.transaction.annotation.Transactional;

public interface QuizService {
    CreateQuizResponse createQuiz(Long userId, CreateQuizRequest request);

    QuizDetailResponse getQuizDetail(Long quizId);

    // 3. 시험지+QA 게시판 통합 조회
    @Transactional(readOnly = true) // 조회이므로 readOnly = true
    QuizQaResponse getQuizWithQa(Long quizId);
}
