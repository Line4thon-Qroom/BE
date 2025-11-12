package com.likelion.server.domain.qa.service;

import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.entity.QaComment;
import com.likelion.server.domain.qa.entity.QaPost;
import com.likelion.server.domain.qa.exception.QaNotFoundException;
import com.likelion.server.domain.qa.exception.QaPostNotFoundException;
import com.likelion.server.domain.qa.repository.QaBoardRepository;
import com.likelion.server.domain.qa.repository.QaCommentRepository;
import com.likelion.server.domain.qa.repository.QaPostRepository;
import com.likelion.server.domain.qa.web.dto.*;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QaServiceImpl implements QaService {

    private final QaPostRepository qaPostRepository;
    private final QaBoardRepository qaBoardRepository;
    private final QaCommentRepository qaCommentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public QaPostResponse createPost(QaPostRequest request, Long currentUserId) {

        // User 조회
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + currentUserId));

        // QaBoard 조회
        QaBoard board = qaBoardRepository.findById(request.boardId())
                .orElseThrow(QaNotFoundException::new);

        // QaPost 엔티티 생성
        QaPost newPost = QaPost.builder()
                .board(board)
                .writer(currentUser)
                .content(request.content())
                .isAnonymous(request.isAnonymous() != null ? request.isAnonymous() : false)
                .build();

        // 엔티티 저장
        QaPost savedPost = qaPostRepository.save(newPost);

        // return
        return new QaPostResponse(savedPost);
    }

    @Override
    @Transactional
    public QaCommentResponse createComment(QaCommentRequest request, Long currentUserId) {

        // 사용자 조회
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + currentUserId));

        // 부모 게시글(QaPost) 조회
        QaPost parentPost = qaPostRepository.findById(request.postId())
                .orElseThrow(QaPostNotFoundException::new);

        // 댓글 엔티티 생성
        QaComment newComment = QaComment.builder()
                .post(parentPost)
                .user(currentUser)
                .content(request.content())
                .isAnonymous(request.isAnonymous() != null ? request.isAnonymous() : false)
                .build();

        // 댓글 저장
        QaComment savedComment = qaCommentRepository.save(newComment);

        // Return
        return new QaCommentResponse(savedComment);
    }

    @Override
    public QaBoardRefreshResponse getBoard(Long quizId) {

        // quiz_id로 QaBoard 조회
        QaBoard board = qaBoardRepository.findByQuizId(quizId)
                .orElseThrow(QaNotFoundException::new);

        // board의 모든 QaPost 조회
        List<QaPost> posts = qaPostRepository.findAllByBoard(board);

        // 각 QaPost를 PostDto로 변환
        List<QaBoardRefreshResponse.PostDto> postDtos = posts.stream()
                .map(post -> {
                    // 각 post의 댓글 수 조회
                    Integer commentsCount = qaCommentRepository.countByPost(post);
                    return new QaBoardRefreshResponse.PostDto(post, commentsCount);
                })
                .toList();

        // return
        return new QaBoardRefreshResponse(board, postDtos);
    }
}
