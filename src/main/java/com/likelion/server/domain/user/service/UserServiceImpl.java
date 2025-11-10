package com.likelion.server.domain.user.service;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.group.entity.Member;
import com.likelion.server.domain.group.repository.GroupMemberRepository;
import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.repository.QaBoardRepository;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizOption;
import com.likelion.server.domain.quiz.entity.QuizResult;
import com.likelion.server.domain.quiz.entity.QuizUserAnswer;
import com.likelion.server.domain.quiz.repository.QuizOptionRepository;
import com.likelion.server.domain.quiz.repository.QuizRepository;
import com.likelion.server.domain.quiz.repository.QuizResultRepository;
import com.likelion.server.domain.quiz.exception.QuizResultNotFoundException;
import com.likelion.server.domain.quiz.exception.QuizUserAnswerNotFoundException;
import com.likelion.server.domain.quiz.repository.QuizUserAnswerRepository;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.exception.UserNicknameDuplicatedException;
import com.likelion.server.domain.user.exception.UserPasswordMismatchException;
import com.likelion.server.domain.user.exception.UserNotFoundException;
import com.likelion.server.domain.user.repository.UserRepository;
import com.likelion.server.domain.user.web.dto.*;
import com.likelion.server.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.likelion.server.domain.user.web.dto.MyPageResponse;
import java.util.List;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 회원가입 / 로그인 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final GroupMemberRepository memberRepository;
    private final QuizRepository quizRepository;
    private final QaBoardRepository qaBoardRepository;
    private final QuizResultRepository quizResultRepository;

    private final QuizUserAnswerRepository quizUserAnswerRepository;
    private final QuizOptionRepository quizOptionRepository;

    // 회원가입
    @Override
    public UserResponse signup(SignupRequest request) {
        // 중복 닉네임 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserNicknameDuplicatedException();
        }

        // 비밀번호 일치 검사
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new UserPasswordMismatchException();
        }

        // 유저 생성 및 저장
        User user = User.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getNickname());
    }


    // 로그인
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByNickname(request.getNickname())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserPasswordMismatchException();
        }

        // Access / Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getNickname());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // Refresh Token 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // DTO 형태로 반환
        return new LoginResponse(
                "로그인 성공",
                accessToken,
                refreshToken,
                new UserResponse(user.getId(), user.getNickname())
        );
    }

    // 홈화면 조회
    @Transactional(readOnly = true)
    @Override
    public HomeResponse getHome(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 사용자가 속한 모든 그룹 조회
        List<Group> userGroups = memberRepository.findAllByUser(user)
                .stream()
                .map(Member::getGroup)
                .toList();

        // [groups] DTO 목록 생성 (Quiz Room)
        List<HomeResponse.GroupDto> groupDtos = userGroups.stream()
                .map(group -> {
                    Integer memberCount = memberRepository.countByGroup(group);
                    return new HomeResponse.GroupDto(group, memberCount);
                })
                .collect(Collectors.toList());

        // [exam_schedule] DTO 목록 생성 (Exam Date)
        List<HomeResponse.ExamScheduleDto> scheduleDtos = userGroups.stream()
                .filter(group -> group.getExamDate() != null && !group.getExamDate().isEmpty())
                .map(HomeResponse.ExamScheduleDto::new)
                .collect(Collectors.toList());

        // [qa_board] DTO 목록 생성 (Q&A 게시판)
        List<HomeResponse.QaBoardDto> qaBoardDtos = new ArrayList<>();

        // 사용자가 속한 그룹의 모든 퀴즈를 조회
        List<Quiz> allQuizzes = userGroups.stream()
                .flatMap(group -> quizRepository.findAllByGroup(group).stream())
                .toList();

        // 각 퀴즈를 순회하며 QA게시판과진행률(Progress) 찾기
        for (Quiz quiz : allQuizzes) {
            Optional<QaBoard> qaBoardOpt = qaBoardRepository.findByQuizId(quiz.getId());

            if (qaBoardOpt.isPresent()) {
                QaBoard qaBoard = qaBoardOpt.get();

                // 퀴즈에 대한 점수(QuizResult) 조회
                Optional<QuizResult> resultOpt = quizResultRepository.findByUserAndQuiz(user, quiz);

                String progress = calculateProgress(user, quiz);

                qaBoardDtos.add(new HomeResponse.QaBoardDto(qaBoard, progress));
            }
        }

        // return
        return new HomeResponse(groupDtos, qaBoardDtos, scheduleDtos);
    }

    // 마이페이지 조회
    @Transactional(readOnly = true)
    @Override
    public MyPageResponse getMyPage(Long userId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 사용자가 푼 모든 퀴즈 결과 조회
        List<QuizResult> results = quizResultRepository.findAllByUser(user);

        // return
        return new MyPageResponse(user, results);
    }

    // 오답노트 조회
    @Transactional(readOnly = true)
    @Override
    public WrongNoteDetailResponse getWrongNoteDetail(Long userId, Long quizId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 퀴즈 결과(요약) 조회
        QuizResult quizResult = quizResultRepository.findByUserAndQuizId(user, quizId)
                .orElseThrow(QuizResultNotFoundException::new);

        // 틀린 문제 목록(QuizUserAnswer) 조회
        List<QuizUserAnswer> wrongAnswers = quizUserAnswerRepository.findAllByQuizResultAndIsCorrect(quizResult, false);

        // 틀린 문제 목록 DTO로 변환
        List<WrongNoteDetailResponse.WrongQuestionDto> wrongQuestionDtos = wrongAnswers.stream()
                .map(answer -> {
                    List<QuizOption> options = Collections.emptyList();

                    if (answer.getQuestion().getType() == com.likelion.server.domain.quiz.entity.enums.Type.MULTIPLE_CHOICE) {
                        options = quizOptionRepository.findAllByQuestion(answer.getQuestion());
                    }
                    return WrongNoteDetailResponse.WrongQuestionDto.from(answer, options);
                })
                .collect(Collectors.toList());

        // return
        return new WrongNoteDetailResponse(quizResult, wrongQuestionDtos);
    }

    // 오답노트 작성/수정
    @Transactional
    @Override
    public UpdateWrongNoteResponse updateWrongNote(Long userId, Long questionId, UpdateWrongNoteRequest request) {

        Long quizResultId = request.quizResultId();

        QuizUserAnswer userAnswer = quizUserAnswerRepository.findByQuizResultIdAndQuestionId(quizResultId, questionId)
                .orElseThrow(QuizUserAnswerNotFoundException::new);

        if (!userAnswer.getUser().getId().equals(userId)) {
            throw new UserNotFoundException();
        }

        userAnswer.updateReview(
                request.memo()
        );

        return new UpdateWrongNoteResponse(userAnswer);
    }

    // progress 계산 메서드
    private String calculateProgress(User user, Quiz quiz) {
        // 퀴즈에 대한 점수(QuizResult) 조회
        Optional<QuizResult> resultOpt = quizResultRepository.findByUserAndQuiz(user, quiz);

        int total = (quiz.getTotalQuestions() != null) ? quiz.getTotalQuestions() : 0;

        if (resultOpt.isPresent() && total > 0) {
            QuizResult result = resultOpt.get();
            int correct = result.getCorrectCount();
            long percent = Math.round((double) correct * 100 / total);
            return String.format("%d/%d (%d%%)", correct, total, percent);
        } else {
            // 퀴즈를 안 풀었거나, totalQuestions가 0 또는 null인 경우
            return String.format("0/%d (0%%)", total);
        }
    }
}
