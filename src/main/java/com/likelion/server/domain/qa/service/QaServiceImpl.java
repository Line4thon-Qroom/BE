package com.likelion.server.domain.qa.service;

import com.likelion.server.domain.qa.entity.QaBoard;
import com.likelion.server.domain.qa.entity.QaPost;
import com.likelion.server.domain.qa.exception.QaNotFoundException;
import com.likelion.server.domain.qa.repository.QaBoardRepository;
import com.likelion.server.domain.qa.repository.QaPostRepository;
import com.likelion.server.domain.qa.web.dto.QaPostRequest;
import com.likelion.server.domain.qa.web.dto.QaPostResponse;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QaServiceImpl implements QaService {

    private final QaPostRepository qaPostRepository;
    private final QaBoardRepository qaBoardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public QaPostResponse createPost(QaPostRequest request, Long currentUserId) {

        // User 조회
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다: " + currentUserId));

        // QaBoard 조회
        QaBoard board = qaBoardRepository.findById(request.getBoardId())
                .orElseThrow(QaNotFoundException::new);

        // QaPost 엔티티 생성
        QaPost newPost = QaPost.builder()
                .board(board)
                .writer(currentUser)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        // 엔티티 저장
        QaPost savedPost = qaPostRepository.save(newPost);

        // return
        return new QaPostResponse(savedPost);
    }
}
