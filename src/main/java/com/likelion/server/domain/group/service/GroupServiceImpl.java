package com.likelion.server.domain.group.service;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.group.entity.Member;
import com.likelion.server.domain.group.entity.enums.Role;
import com.likelion.server.domain.group.exception.AlreadyGroupMemberException;
import com.likelion.server.domain.group.exception.GroupNotFoundException;
import com.likelion.server.domain.group.exception.GroupPermissionDeniedException;
import com.likelion.server.domain.group.repository.GroupMemberRepository;
import com.likelion.server.domain.group.repository.StudyGroupRepository;
import com.likelion.server.domain.group.web.dto.CreateGroupRequest;
import com.likelion.server.domain.group.web.dto.CreateGroupResponse;
import com.likelion.server.domain.group.web.dto.GroupJoinResponse;
import com.likelion.server.domain.group.web.dto.GroupMainResponse;
import com.likelion.server.domain.pdf.entity.Pdf;
import com.likelion.server.domain.pdf.repository.PdfRepository;
import com.likelion.server.domain.pdf.service.PdfService;
import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.entity.QaPost;
import com.likelion.server.domain.qa.repository.QaBoardRepository;
import com.likelion.server.domain.qa.repository.QaCommentRepository;
import com.likelion.server.domain.qa.repository.QaPostRepository;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.quiz.repository.*;
import com.likelion.server.domain.ranking.entity.GroupRanking;
import com.likelion.server.domain.ranking.repository.GroupRankingRepository;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImpl implements GroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EntityManager em;
    private final UserRepository userRepository;
    private final PdfRepository pdfRepository;
    private final PdfService pdfService;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final QuizResultRepository quizResultRepository;
    private final QuizUserAnswerRepository quizUserAnswerRepository;
    private final QaBoardRepository qaBoardRepository;
    private final QaPostRepository qaPostRepository;
    private final QaCommentRepository qaCommentRepository;
    private final GroupRankingRepository groupRankingRepository;

    private static final char[] CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final SecureRandom RND = new SecureRandom();

    // 그룹 생성
    @Override
    public CreateGroupResponse create(Long userId, CreateGroupRequest createGroupRequest) {
        LocalDate examDate = null;
        if (createGroupRequest.examDate() != null && !createGroupRequest.examDate().isBlank()) {
            examDate = LocalDate.parse(createGroupRequest.examDate());
        }
        String code = generateUniqueCode(4);
        Group group = Group.builder()
                .name(createGroupRequest.name())
                .examDate(String.valueOf(examDate))
                .imageNum(createGroupRequest.imageNum())
                .groupCode(code)
                .build();
        Group saveGroup = studyGroupRepository.save(group);
        Member leader = new Member(
                em.getReference(Group.class, saveGroup.getId()),
                em.getReference(User.class, userId),
                Role.LEADER
        );
        groupMemberRepository.save(leader);
        return new CreateGroupResponse(
                saveGroup.getId(),
                saveGroup.getName(),
                saveGroup.getExamDate() != null ? saveGroup.getExamDate().toString() : null,
                saveGroup.getGroupCode(),
                saveGroup.getImageNum()
        );
    }

    // 그룹 입장
    @Override
    public GroupJoinResponse joinByCode(Long userId, String groupCode) {
        Group group = studyGroupRepository.findByGroupCode(groupCode);
        if (group == null) {
            throw new GroupNotFoundException();
        }
        if (groupMemberRepository.existsByGroupIdAndUserId(group.getId(), userId)) {
            throw new AlreadyGroupMemberException();
        }
        Member member = new Member(
                group,
                em.getReference(User.class, userId),
                Role.MEMBER
        );
        groupMemberRepository.save(member);
        return new GroupJoinResponse(group.getId());
    }

    // 그룹 퇴장(MEMBER)
    @Override
    public void leaveGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Group group = studyGroupRepository.findById(groupId)
                .orElseThrow(GroupNotFoundException::new);
        Member member = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new GroupNotFoundException());
        if (member.getRole() == Role.LEADER) {
            throw new GroupPermissionDeniedException();
        }
        deletePersonalQuizActivity(user, group);
        groupMemberRepository.delete(member);
        recalculateRankPositions(group);
    }

    // 그룹삭제(LEADER)
    @Override
    public void deleteGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Group group = studyGroupRepository.findById(groupId)
                .orElseThrow(GroupNotFoundException::new);
        Member member = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new GroupNotFoundException());
        if (member.getRole() == Role.MEMBER) {
            throw new GroupPermissionDeniedException();
        }
        deleteAllGroupData(group);
        studyGroupRepository.delete(group);
    }

    // 스터디룸 메인화면 조회
    @Override
    @Transactional(readOnly = true)
    public GroupMainResponse getGroupMain(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Group group = studyGroupRepository.findById(groupId)
                .orElseThrow(GroupNotFoundException::new);

        // Group 정보 (멤버 목록, 내 역할 포함)
        Member myMemberInfo = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new GroupPermissionDeniedException());
        String myRole = myMemberInfo.getRole().toString();
        List<Member> allMembers = groupMemberRepository.findAllByGroup(group);
        GroupMainResponse.GroupDto groupDto = new GroupMainResponse.GroupDto(group, myRole, allMembers);

        // PDF 목록
        List<Pdf> pdfs = pdfRepository.findAllByGroup(group);
        List<GroupMainResponse.PdfDto> pdfDtos = pdfs.stream()
                .map(GroupMainResponse.PdfDto::new)
                .toList();

        // 퀴즈 목록 (참여자 수)
        List<Quiz> quizzes = quizRepository.findAllByGroup(group);
        List<GroupMainResponse.QuizDto> quizDtos = quizzes.stream()
                .map(quiz -> {
                    Long participantsCount = quizResultRepository.countDistinctUserByQuiz(quiz);
                    return new GroupMainResponse.QuizDto(quiz, participantsCount);
                })
                .toList();

        // QA 게시판 목록 (내 진행률)
        List<QaBoard> qaBoards = qaBoardRepository.findAllByGroup(group);
        List<GroupMainResponse.QaBoardDto> qaBoardDtos = qaBoards.stream()
                .map(board -> {
                    String progress = calculateProgress(user, board.getQuiz());
                    return new GroupMainResponse.QaBoardDto(board, progress);
                })
                .toList();

        // 랭킹 (내 등수 + 전체 목록)
        Integer myRank = groupRankingRepository.findByGroupAndUser(group, user)
                .map(GroupRanking::getRankPosition)
                .orElse(null);
        List<GroupRanking> allRanks = groupRankingRepository.findAllByGroupOrderByTotalScoreDesc(group);

        GroupMainResponse.RankingDto rankingDto = GroupMainResponse.RankingDto.from(myRank, allRanks);

        // return
        return new GroupMainResponse(groupDto, pdfDtos, quizDtos, qaBoardDtos, rankingDto);
    }

    private String generateUniqueCode(int length) {
        String code;
        int attempts = 0;
        do {
            code = randomCode(length);
            if (++attempts > 20) {
                throw new IllegalStateException("그룹 코드 생성에 실패했습니다. 다시 시도해주세요.");
            }
        } while (studyGroupRepository.existsByGroupCode(code));
        return code;
    }

    private String randomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(CODE_CHARS[RND.nextInt(CODE_CHARS.length)]);
        return sb.toString();
    }

    private void deletePersonalQuizActivity(User user, Group group) {
        List<Quiz> quizzesInGroup = quizRepository.findAllByGroup(group);
        if (quizzesInGroup.isEmpty()) return;
        List<QuizResult> myResults = quizResultRepository.findAllByUserAndQuizIn(user, quizzesInGroup);
        if (!myResults.isEmpty()) {
            quizUserAnswerRepository.deleteAllByQuizResultIn(myResults);
            quizResultRepository.deleteAll(myResults);
        }
        // 랭킹 삭제
        groupRankingRepository.deleteByUserAndGroup(user, group);
    }

    private void deleteAllGroupData(Group group) {
        List<Quiz> quizzesInGroup = quizRepository.findAllByGroup(group);
        List<QaBoard> boardsInGroup = qaBoardRepository.findAllByGroup(group);

        // QA 데이터 먼저 삭제
        if (!boardsInGroup.isEmpty()) {
            List<QaPost> allPosts = qaPostRepository.findAllByBoardIn(boardsInGroup);
            if (!allPosts.isEmpty()) {
                qaCommentRepository.deleteAllByPostIn(allPosts);
                qaPostRepository.deleteAll(allPosts);
            }
            qaBoardRepository.deleteAll(boardsInGroup);
        }

        // 퀴즈 데이터 삭제
        if (!quizzesInGroup.isEmpty()) {
            List<QuizResult> allResults = quizResultRepository.findAllByQuizIn(quizzesInGroup);
            if (!allResults.isEmpty()) {
                quizUserAnswerRepository.deleteAllByQuizResultIn(allResults);
                quizResultRepository.deleteAll(allResults);
            }
            quizzesInGroup.forEach(quiz -> {
                quizQuestionRepository.findAllByQuiz(quiz).forEach(quizOptionRepository::deleteAllByQuestion);
                quizQuestionRepository.deleteAllByQuiz(quiz);
            });
            quizRepository.deleteAll(quizzesInGroup);
        }

        // PDF 데이터 삭제 (S3 포함)
        List<Pdf> pdfsInGroup = pdfRepository.findAllByGroupId(group.getId());
        for (Pdf pdf : pdfsInGroup) {
            pdfService.delete(pdf.getId());
        }

        // 랭킹 및 멤버 삭제
        groupRankingRepository.deleteAllByGroup(group);
        groupMemberRepository.deleteAll(groupMemberRepository.findAllByGroup(group));
    }

    private String calculateProgress(User user, Quiz quiz) {
        Optional<QuizResult> resultOpt = quizResultRepository.findByUserAndQuiz(user, quiz);
        int total = (quiz.getTotalQuestions() != null) ? quiz.getTotalQuestions() : 0;
        if (resultOpt.isPresent() && total > 0) {
            QuizResult result = resultOpt.get();
            int correct = result.getCorrectCount();
            long percent = Math.round((double) correct * 100 / total);
            return String.format("%d/%d (%d%%)", correct, total, percent);
        } else {
            return String.format("0/%d (0%%)", total);
        }
    }

    private void recalculateRankPositions(Group group) {
        List<GroupRanking> rankings = groupRankingRepository.findAllByGroupOrderByTotalScoreDesc(group);

        int currentRank = 1;
        int sameScoreCount = 0;
        Integer previousScore = null;

        for (GroupRanking rank : rankings) {
            if (previousScore == null || !rank.getTotalScore().equals(previousScore)) {
                currentRank += sameScoreCount;
                sameScoreCount = 0;
            }

            rank.setRankPosition(currentRank);
            sameScoreCount++;
            previousScore = rank.getTotalScore();
        }
    }
}