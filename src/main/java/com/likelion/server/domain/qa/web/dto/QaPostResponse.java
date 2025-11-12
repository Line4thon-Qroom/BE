package com.likelion.server.domain.qa.web.dto;

import com.likelion.server.domain.qa.entity.QaPost;
import com.likelion.server.domain.user.entity.User;

import java.time.format.DateTimeFormatter;

public record QaPostResponse(
        Long id,
        Long boardId,
        UserDto user,
        String content,
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

    public QaPostResponse(QaPost qaPost) {
        this(
                qaPost.getId(),
                qaPost.getBoard().getId(),
                qaPost.getIsAnonymous()
                        ? new UserDto(qaPost.getWriter().getId()) // 익명
                        : new UserDto(qaPost.getWriter()),       // 실명
                qaPost.getContent(),
                qaPost.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
