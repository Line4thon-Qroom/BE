package com.likelion.server.domain.qa.service;

import com.likelion.server.domain.qa.web.dto.QaCommentRequest;
import com.likelion.server.domain.qa.web.dto.QaCommentResponse;
import com.likelion.server.domain.qa.web.dto.QaPostRequest;
import com.likelion.server.domain.qa.web.dto.QaPostResponse;
import com.likelion.server.domain.user.entity.User;

public interface QaService {
    QaPostResponse createPost(QaPostRequest request, Long currentUserId);
    QaCommentResponse createComment(QaCommentRequest request, Long currentUserId);
}
