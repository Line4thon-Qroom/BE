package com.likelion.server.domain.qa.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QaPostRequest(
        @NotNull(message = "게시판 ID는 필수입니다.")
        Long boardId,

        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        String content
) {}
