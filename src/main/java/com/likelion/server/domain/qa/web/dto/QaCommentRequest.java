package com.likelion.server.domain.qa.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QaCommentRequest(
        @NotNull(message = "게시글 ID는 필수입니다.")
        @JsonProperty("post_id")
        Long postId,

        @NotBlank(message = "댓글 내용은 필수입니다.")
        String content
) {
}