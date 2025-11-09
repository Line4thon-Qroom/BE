package com.likelion.server.domain.qa.service;

import com.likelion.server.domain.qa.web.dto.*;

public interface QaService {
    QaPostResponse createPost(QaPostRequest request, Long currentUserId);
    QaCommentResponse createComment(QaCommentRequest request, Long currentUserId);
    QaBoardRefreshResponse getBoard(Long quizId);
}
