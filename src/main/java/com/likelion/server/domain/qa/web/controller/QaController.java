package com.likelion.server.domain.qa.web.controller;

import com.likelion.server.domain.qa.service.QaService;
import com.likelion.server.domain.qa.web.dto.QaCommentRequest;
import com.likelion.server.domain.qa.web.dto.QaCommentResponse;
import com.likelion.server.domain.qa.web.dto.QaPostRequest;
import com.likelion.server.domain.qa.web.dto.QaPostResponse;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qa")
public class QaController {

    private final QaService qaService;

    // QA 게시글 등록
    @PostMapping("/post")
    public SuccessResponse<QaPostResponse> createPost(
            @Valid @RequestBody QaPostRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        QaPostResponse data = qaService.createPost(request, userId);
        return SuccessResponse.created(data);
    }

    // QA 게시글 댓글 등록
    @PostMapping("/comment") //
    public SuccessResponse<QaCommentResponse> createComment(
            @Valid @RequestBody QaCommentRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        QaCommentResponse data = qaService.createComment(request, userId);
        return SuccessResponse.created(data);
    }
}
