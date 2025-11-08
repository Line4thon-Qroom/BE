package com.likelion.server.domain.group.web.dto;

import jakarta.validation.constraints.*;

public record CreateGroupRequest(
        @NotBlank @Size(min = 1, max = 10)
        String name,

        String examDate,

        @NotNull @Min(1) @Max(10)
        Integer imageNum
) {}
