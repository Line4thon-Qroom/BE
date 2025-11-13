package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.quiz.web.dto.*;
import org.springframework.transaction.annotation.Transactional;

public interface QuizService {

    // AI 생성 퀴즈
    CreateQuizResponse createQuiz(Long userId, CreateQuizRequest request);

    // 사용자 생성 퀴즈
    CreateQuizResponse createUserQuiz(Long userId, CreateUserQuizRequest request);

    // 퀴즈 상세 조회
    QuizDetailResponse getQuizDetail(Long quizId);

    // 시험지+QA 게시판 통합 조회
    @Transactional(readOnly = true) // 조회이므로 readOnly = true
    QuizQaResponse getQuizWithQa(Long quizId);
}
