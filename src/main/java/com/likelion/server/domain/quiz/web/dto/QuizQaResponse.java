package com.likelion.server.domain.quiz.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.entity.QaComment;
import com.likelion.server.domain.qa.entity.QaPost;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizOption;
import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.user.entity.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record QuizQaResponse(
        QuizDto quiz,
        @JsonProperty("qa_board") QaBoardDto qaBoard
) {
    public record QuizDto(
            Long id,
            String title,
            String difficulty,
            Integer round,
            @JsonProperty("total_questions") Integer totalQuestions,
            @JsonProperty("group_name") String groupName,
            List<QuestionDto> questions
    ) {
        public QuizDto(Quiz quiz, List<QuestionDto> questions) {
            this(
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getDifficulty() != null ? quiz.getDifficulty().getKorean() : null,
                    quiz.getRound(),
                    quiz.getTotalQuestions(),
                    quiz.getGroup() != null ? quiz.getGroup().getName() : null,
                    questions
            );
        }
    }

    public record QuestionDto(
            Long id,
            String type,
            @JsonProperty("question_text") String questionText,
            @JsonProperty("correct_answer") String correctAnswer,
            String explanation,
            List<OptionDto> options
    ) {
        public QuestionDto(QuizQuestion question, List<OptionDto> options) {
            this(
                    question.getId(),
                    question.getType().getDisplayName(),
                    question.getQuestionText(),
                    question.getCorrectAnswer(),
                    question.getExplanation(),
                    (options == null || options.isEmpty()) ? null : options
            );
        }
    }

    public record OptionDto(
            Long id,
            @JsonProperty("option_text") String optionText
    ) {
        public OptionDto(QuizOption option) {
            this(option.getId(), option.getOptionText()); // QuizOption
        }
    }

    public record QaBoardDto(
            @JsonProperty("board_id") Long boardId,
            @JsonProperty("board_title") String boardTitle,
            @JsonProperty("board_type") String boardType,
            List<PostDto> posts
    ) {
        public QaBoardDto(QaBoard board, List<PostDto> posts) {
            this(
                    board.getId(),
                    board.getBoardName(),
                    "시험지", // 예시의 "시험지" 타입
                    posts
            );
        }
    }

    public record PostDto(
            Long id,
            UserNicknameDto user,
            String content,
            @JsonProperty("created_at") String createdAt,
            List<CommentDto> comments
    ) {
        public PostDto(QaPost post, UserNicknameDto userDto, List<CommentDto> comments) {
            this(
                    post.getId(),
                    userDto,
                    post.getContent(),
                    post.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    comments
            );
        }
    }

    public record CommentDto(
            Long id,
            UserNicknameDto user,
            String content,
            @JsonProperty("created_at") String createdAt
    ) {
        public CommentDto(QaComment comment, UserNicknameDto userDto) {
            this(
                    comment.getId(),
                    userDto,
                    comment.getContent(),
                    comment.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
        }
    }

    public record UserNicknameDto(
            String nickname
    ) {
        public UserNicknameDto(User user) {
            this(user.getNickname());
        }
    }
}
