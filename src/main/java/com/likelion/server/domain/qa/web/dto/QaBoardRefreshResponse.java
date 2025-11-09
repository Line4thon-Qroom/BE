package com.likelion.server.domain.qa.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.entity.QaPost;
import com.likelion.server.domain.user.entity.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record QaBoardRefreshResponse(
        @JsonProperty("board_id") Long boardId,
        List<PostDto> posts
) {
    public record PostDto(
            Long id,
            String title,
            UserDto user,
            @JsonProperty("comments_count") Integer commentsCount, // "comments_count": 3
            @JsonProperty("created_at") String createdAt
    ) {
        public PostDto(QaPost post, Integer commentsCount) {
            this(
                    post.getId(),
                    post.getTitle(),
                    new UserDto(post.getWriter()),
                    commentsCount,
                    post.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
        }
    }

    public record UserDto(
            String nickname
    ) {
        public UserDto(User user) {
            this(user.getNickname());
        }
    }

    public QaBoardRefreshResponse(QaBoard board, List<PostDto> postDtos) {
        this(board.getId(), postDtos);
    }
}
