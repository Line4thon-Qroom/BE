package com.likelion.server.domain.group.web.dto;

import jakarta.validation.constraints.NotBlank;

public record GroupJoinRequest(
        @NotBlank(message = "그룹코드는 필수입니다.")
        String groupCode
) {}