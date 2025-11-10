package com.likelion.server.domain.user.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequest(
        @Size(min = 2, max = 10, message = "닉네임은 2~10자 이내로 입력해주세요.")
        String nickname,

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{4,15}$",
                message = "비밀번호는 4자 이상 15자 이하이며, 영문자와 숫자를 최소 1개 이상 포함해야 합니다.")
        String password,

        @JsonProperty("passwordCheck")
        String passwordCheck
) {
}