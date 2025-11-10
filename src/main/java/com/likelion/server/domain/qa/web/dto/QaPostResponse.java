package com.likelion.server.domain.qa.web.dto;

import com.likelion.server.domain.qa.entity.QaPost;
import com.likelion.server.domain.user.entity.User;

import java.time.format.DateTimeFormatter;

public record QaPostResponse(
        Long id,
        Long boardId,
        UserDto user,
        String title,
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
    }

    public QaPostResponse(QaPost qaPost) {
        this(
                qaPost.getId(),
                qaPost.getBoard().getId(),
                new UserDto(qaPost.getWriter()),
                qaPost.getTitle(),
                qaPost.getContent(),
                qaPost.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
