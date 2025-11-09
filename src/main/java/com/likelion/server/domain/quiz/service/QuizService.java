package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.quiz.web.dto.CreateQuizRequest;
import com.likelion.server.domain.quiz.web.dto.CreateQuizResponse;
import com.likelion.server.domain.quiz.web.dto.QuizDetailResponse;

public interface QuizService {
    CreateQuizResponse createQuiz(Long userId, CreateQuizRequest request);

    QuizDetailResponse getQuizDetail(Long quizId);
}
