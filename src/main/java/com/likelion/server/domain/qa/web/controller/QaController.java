package com.likelion.server.domain.qa.web.controller;

import com.likelion.server.domain.qa.service.QaService;
import com.likelion.server.domain.qa.web.dto.*;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // QA 게시판 새로고침 (게시글/댓글 목록 조회)
    @GetMapping("/board/{quiz_id}")
    public SuccessResponse<QaBoardRefreshResponse> getBoard(
            @PathVariable("quiz_id") Long quizId
    ) {
        QaBoardRefreshResponse data = qaService.getBoard(quizId);
        return SuccessResponse.ok(data);
    }
}
