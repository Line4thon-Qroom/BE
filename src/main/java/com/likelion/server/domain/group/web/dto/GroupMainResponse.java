package com.likelion.server.domain.group.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.group.entity.Member;
import com.likelion.server.domain.pdf.entity.Pdf;
import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.ranking.entity.GroupRanking;
import java.util.List;
import java.util.stream.Collectors;

// 그룹 스터디룸 메인화면 조회 응답 DTO
public record GroupMainResponse(
        GroupDto group,
        List<PdfDto> pdfs,
        List<QuizDto> quizzes,
        @JsonProperty("qa_boards") List<QaBoardDto> qaBoards,
        RankingDto ranking
) {
    // Group (그룹 정보 + 멤버 목록)
    public record GroupDto(
            Long id,
            String name,
            String role,
            String examDate,
            String groupCode,
            Integer memberCount,
            List<MemberDto> members
    ) {
        public GroupDto(Group group, String myRole, List<Member> members) {
            this(
                    group.getId(),
                    group.getName(),
                    myRole,
                    group.getExamDate(),
                    group.getGroupCode(),
                    members.size(),
                    members.stream().map(MemberDto::new).collect(Collectors.toList())
            );
        }
    }

    public record MemberDto(Long id, String nickname) {
        public MemberDto(Member member) { this(member.getUser().getId(), member.getUser().getNickname()); }
    }

    // PDFs (PDF 목록)
    public record PdfDto(
            Long id,
            String title
    ) {
        public PdfDto(Pdf pdf) {
            this(pdf.getId(), pdf.getFileName());
        }
    }

    // Quizzes (퀴즈 목록 + 참여자 수)
    public record QuizDto(
            Long id,
            String title,
            String difficulty,
            @JsonProperty("participants_count") Long participantsCount
    ) {
        public QuizDto(Quiz quiz, Long participantsCount) {
            this(
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getDifficulty() != null ? quiz.getDifficulty().getKorean() : null,
                    participantsCount
            );
        }
    }

    // QA Boards (QA 목록 + 내 정답률)
    public record QaBoardDto(
            @JsonProperty("board_id") Long boardId,
            @JsonProperty("quiz_id") Long quizId,
            String title,
            String progress
    ) {
        public QaBoardDto(QaBoard board, String progress) {
            this(
                    board.getId(),
                    board.getQuiz().getId(),
                    board.getBoardName(),
                    progress
            );
        }
    }

    // RankDto
    public record RankDto(
            Integer position,
            String nickname,
            @JsonProperty("total_correct") Integer totalCorrect
    ) {
        public RankDto(GroupRanking rank) {
            this(
                    rank.getRankPosition(),
                    rank.getUser().getNickname(),
                    rank.getTotalScore()
            );
        }
    }

    // RankingDto
    public record RankingDto(
            @JsonProperty("my_rank") Integer myRank,
            @JsonProperty("all_ranks") List<RankDto> allRanks
    ) {
        public static RankingDto from(Integer myRank, List<GroupRanking> ranks) {
            List<RankDto> rankDtos = ranks.stream()
                    .map(RankDto::new)
                    .collect(Collectors.toList());

            return new RankingDto(myRank, rankDtos);
        }
    }
}