package com.likelion.server.domain.qa.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.server.domain.qa.entity.QaComment;
import com.likelion.server.domain.user.entity.User;
import java.time.format.DateTimeFormatter;

public record QaCommentResponse(
        Long id,
        @JsonProperty("post_id")
        Long postId,
        UserDto user,
        String content,
        @JsonProperty("created_at")
        String createdAt
) {
    public record UserDto(
            Long id,
            String nickname
    ) {
        public UserDto(User user) {
            this(user.getId(), user.getNickname());
        }

        public UserDto(Long userId) {
            this(userId, "익명");
        }
    }

    public QaCommentResponse(QaComment qaComment) {
        this(
                qaComment.getId(),
                qaComment.getPost().getId(),
                qaComment.getIsAnonymous()
                        ? new UserDto(qaComment.getUser().getId()) // 익명
                        : new UserDto(qaComment.getUser()),       // 실명
                qaComment.getContent(),
                qaComment.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}