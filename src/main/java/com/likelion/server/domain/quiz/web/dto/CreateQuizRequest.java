package com.likelion.server.domain.quiz.web.dto;

import java.util.List;

public record CreateQuizRequest(
        Long pdf_id,
        String difficulty,
        List<String> question_types,
        Integer total_questions
) {}
