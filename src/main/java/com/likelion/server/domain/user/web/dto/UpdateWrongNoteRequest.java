package com.likelion.server.domain.user.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record UpdateWrongNoteRequest(
        @NotNull
        @JsonProperty("quiz_result_id")
        Long quizResultId,
        String memo
) {
}
