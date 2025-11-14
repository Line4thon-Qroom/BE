package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.pdf.entity.Pdf;
import com.likelion.server.domain.pdf.repository.PdfRepository;
import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.entity.QaComment;
import com.likelion.server.domain.qa.entity.QaPost;
import com.likelion.server.domain.qa.exception.QaNotFoundException;
import com.likelion.server.domain.qa.repository.QaBoardRepository;
import com.likelion.server.domain.qa.repository.QaCommentRepository;
import com.likelion.server.domain.qa.repository.QaPostRepository;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizOption;
import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.quiz.entity.enums.Difficulty;
import com.likelion.server.domain.quiz.entity.enums.Type;
import com.likelion.server.domain.quiz.exception.*;
import com.likelion.server.domain.quiz.repository.QuizOptionRepository;
import com.likelion.server.domain.quiz.repository.QuizRepository;
import com.likelion.server.domain.quiz.repository.QuizQuestionRepository;
import com.likelion.server.domain.quiz.web.dto.CreateQuizRequest;
import com.likelion.server.domain.quiz.web.dto.CreateQuizResponse;
import com.likelion.server.domain.quiz.web.dto.CreateUserQuizRequest;
import com.likelion.server.domain.quiz.web.dto.QuizDetailResponse;
import com.likelion.server.domain.quiz.web.dto.QuizQaResponse;

import com.likelion.server.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final PdfRepository pdfRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QaBoardRepository qaBoardRepository;
    private final AiQuizGenerator aiQuizGenerator;
    private final EntityManager em;

    private final QuizOptionRepository quizOptionRepository;
    private final QaPostRepository qaPostRepository;
    private final QaCommentRepository qaCommentRepository;

    // 세부 로직 분리 메서드
    private Pdf validateAndGetPdf(Long pdfId) {
        return pdfRepository.findById(pdfId)
                .orElseThrow(QuizNotFoundException::new);
    }

    //Quiz 엔티티
    private Quiz createQuizEntity(Pdf pdf, CreateQuizRequest request, int round) {
        if (pdf == null || pdf.getGroup() == null)
            throw new QuizGroupNotFoundException();

        Difficulty difficulty = Difficulty.fromKorean(request.difficulty());
        return Quiz.builder()
                .pdf(pdf)
                .round(round)
                .difficulty(difficulty)
                .questionTypes(String.join(",", request.question_types()))
                .totalQuestions(request.total_questions())
                .title(pdf.getFileName())
                .group(pdf.getGroup())
                .build();
    }

    //QA 엔티티
    private QaBoard createQaBoard(Pdf pdf, Quiz quiz) {
        return QaBoard.builder()
                .quiz(quiz)
                .boardName(pdf.getFileName() + " Quiz 게시판")
                .group(pdf.getGroup())
                .build();
    }

///////////////////////////////////////////////////////////////////////////////////
    // 퀴즈 생성 1 - AI
    @Override
    public CreateQuizResponse createQuiz(Long userId, CreateQuizRequest request) {

        // PDF 검증 및 조회
        Pdf pdf = validateAndGetPdf(request.pdf_id());

        // 라운드 계산
        int round = quizRepository.countByPdfId(pdf.getId()) + 1;

        // 퀴즈 생성 및 저장
        Quiz quiz = createQuizEntity(pdf, request, round);
        try {
            quizRepository.saveAndFlush(quiz);
        } catch (Exception e) {
            throw new QuizSaveFailException();
        }

        // AI 퀴즈 생성
        List<QuizQuestion> generatedQuestions;
        try {
            generatedQuestions = aiQuizGenerator.generateQuestions(pdf, quiz, request);
        } catch (QuizGenerationFailException | QuizInvalidFormatException e) {
            throw e; // 그대로 전달
        } catch (Exception e) {
            e.printStackTrace();
            throw new QuizGenerationFailException();
        }

        if (generatedQuestions == null || generatedQuestions.isEmpty())
            throw new QuizInvalidFormatException();

        // 문제 저장 (Cascade 안 쓰는 경우 명시적으로 saveAll)
        try {
            quizQuestionRepository.saveAll(generatedQuestions);
        } catch (Exception e) {
            throw new QuizSaveFailException();
        }

        // QA 게시판 생성
        QaBoard qaBoard;
        try {
            qaBoard = createQaBoard(pdf, quiz);
            qaBoardRepository.save(qaBoard);
        } catch (Exception e) {
            throw new QuizSaveFailException();
        }

        // 응답 DTO 변환
        return CreateQuizResponse.fromEntity(quiz, qaBoard);
    }


    // 퀴즈 생성 2 - 사용자
    @Override
    public CreateQuizResponse createUserQuiz(Long userId, CreateUserQuizRequest request) {
        // 그룹 유효성 검증
        Group group = em.find(Group.class, request.getGroup_id());
        if (group == null) throw new QuizGroupNotFoundException();


        List<String> types = request.getQuestions().stream()
                .map(CreateUserQuizRequest.UserQuestionRequest::getType)
                .distinct()
                .toList();

        // Quiz 엔티티 생성 (AI·PDF 없이)
        Quiz quiz = Quiz.builder()
                .group(group)
                .title(request.getTitle())
                .round(1)
                .difficulty(Difficulty.중)
                .totalQuestions(request.getQuestions().size())
                .questionTypes(String.join(",", types))
                .build();

        quizRepository.saveAndFlush(quiz);

        // 각 문제 저장
        for (CreateUserQuizRequest.UserQuestionRequest q : request.getQuestions()) {
            Type type = Type.fromKorean(q.getType());

            QuizQuestion question = QuizQuestion.builder()
                    .quiz(quiz)
                    .type(type)
                    .questionText(q.getQuestion_text())
                    .correctAnswer(q.getCorrect_answer())
                    .explanation(q.getExplanation())
                    .build();

            quizQuestionRepository.save(question);

            // 객관식 보기 저장
            if (type == Type.MULTIPLE_CHOICE && q.getOptions() != null) {
                for (CreateUserQuizRequest.OptionRequest opt : q.getOptions()) {
                    QuizOption option = QuizOption.builder()
                            .question(question)
                            .optionText(opt.getOption_text())
                            .build();
                    quizOptionRepository.save(option);
                }
            }
        }

        // QA 게시판 자동 생성
        QaBoard qaBoard = QaBoard.builder()
                .quiz(quiz)
                .group(group)
                .boardName(request.getTitle() + " (made)")
                .build();
        qaBoardRepository.save(qaBoard);

        // 응답 반환
        return CreateQuizResponse.fromEntity(quiz, qaBoard);
    }


    // 2. 퀴즈 상세 조회
    @Override
    @Transactional(readOnly = true)
    public QuizDetailResponse getQuizDetail(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(QuizNotFoundException::new);

        List<QuizQuestion> questions = quizQuestionRepository.findAllByQuizId(quizId);
        if (questions.isEmpty())
            throw new QuizInvalidFormatException(); // 퀴즈 문제 없음

        return QuizDetailResponse.fromEntities(quiz, questions);
    }

    // 3. 시험지+QA 게시판 통합 조회
    @Transactional(readOnly = true)
    @Override
    public QuizQaResponse getQuizWithQa(Long quizId) {

        // Quiz DTO
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(QuizNotFoundException::new);

        List<QuizQaResponse.QuestionDto> questionDtos = quizQuestionRepository.findAllByQuizId(quizId)
                .stream()
                .map(question -> {
                    // 객관식 보기 목록 조회
                    List<QuizQaResponse.OptionDto> optionDtos = quizOptionRepository.findAllByQuestion(question)
                            .stream()
                            .map(QuizQaResponse.OptionDto::new)
                            .collect(Collectors.toList());

                    return new QuizQaResponse.QuestionDto(question, optionDtos);
                })
                .collect(Collectors.toList());

        QuizQaResponse.QuizDto quizDto = new QuizQaResponse.QuizDto(quiz, questionDtos);
        // QA Board DTO
        QaBoard qaBoard = qaBoardRepository.findByQuizId(quizId)
                .orElseThrow(QaNotFoundException::new);

        // 게시글/댓글 모두 생성순 정렬
        List<QaPost> posts = qaPostRepository.findAllByBoardOrderByCreatedAtAsc(qaBoard);
        List<QuizQaResponse.PostDto> postDtos = new ArrayList<>();

        for (QaPost post : posts) {

            // 익명 번호 매핑 로직 (게시글마다 리셋)
            Map<Long, Integer> anonymousMap = new HashMap<>();
            int anonymousCounter = 1;

            // 게시글 작성자 익명 처리
            User postWriter = post.getWriter();
            QuizQaResponse.UserNicknameDto postUserDto;

            if (post.getIsAnonymous()) {
                anonymousMap.put(postWriter.getId(), anonymousCounter++);
                String nickname = "익명 " + anonymousMap.get(postWriter.getId());
                postUserDto = new QuizQaResponse.UserNicknameDto(nickname);
            } else {
                postUserDto = new QuizQaResponse.UserNicknameDto(postWriter);
            }

            // 댓글 목록 조회 및 익명 처리
            List<QaComment> comments = qaCommentRepository.findAllByPostOrderByCreatedAtAsc(post);
            List<QuizQaResponse.CommentDto> commentDtos = new ArrayList<>();

            for (QaComment comment : comments) {
                User commentWriter = comment.getUser();
                QuizQaResponse.UserNicknameDto commentUserDto;

                if (comment.getIsAnonymous()) {
                    if (!anonymousMap.containsKey(commentWriter.getId())) {
                        anonymousMap.put(commentWriter.getId(), anonymousCounter++);
                    }
                    String nickname = "익명 " + anonymousMap.get(commentWriter.getId());
                    commentUserDto = new QuizQaResponse.UserNicknameDto(nickname);
                } else {
                    commentUserDto = new QuizQaResponse.UserNicknameDto(commentWriter);
                }

                commentDtos.add(new QuizQaResponse.CommentDto(comment, commentUserDto));
            }

            postDtos.add(new QuizQaResponse.PostDto(post, postUserDto, commentDtos));
        }

        QuizQaResponse.QaBoardDto qaBoardDto = new QuizQaResponse.QaBoardDto(qaBoard, postDtos);
        return new QuizQaResponse(quizDto, qaBoardDto);
    }
}
