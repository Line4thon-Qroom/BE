package com.likelion.server.domain.group.service;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.group.entity.Member;
import com.likelion.server.domain.group.entity.enums.Role;
import com.likelion.server.domain.group.repository.GroupMemberRepository;
import com.likelion.server.domain.group.repository.StudyGroupRepository;
import com.likelion.server.domain.group.web.dto.CreateGroupRequest;
import com.likelion.server.domain.group.web.dto.CreateGroupResponse;
import com.likelion.server.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImpl implements GroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EntityManager em;

    private static final char[] CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final SecureRandom RND = new SecureRandom();

    @Override
    public CreateGroupResponse create(Long userId, CreateGroupRequest createGroupRequest) {

        // examDate 파싱
        LocalDate examDate = null;
        if (createGroupRequest.examDate() != null && !createGroupRequest.examDate().isBlank()) {
            examDate = LocalDate.parse(createGroupRequest.examDate()); // yyyy-MM-dd
        }

        // GroupCode 생성
        String code = generateUniqueCode(6);

        // Group 생성
        Group group = Group.builder()
                .name(createGroupRequest.name())
                .examDate(String.valueOf(examDate))
                .imageNum(createGroupRequest.imageNum())
                .groupCode(code)
                .build();
        Group saveGroup = studyGroupRepository.save(group);

        // Group leader 등록
        Member leader = new Member(
                em.getReference(Group.class, saveGroup.getId()),
                em.getReference(User.class, userId),
                Role.LEADER
        );
        groupMemberRepository.save(leader);

        // return
        return new CreateGroupResponse(
                saveGroup.getId(),
                saveGroup.getName(),
                saveGroup.getExamDate() != null ? saveGroup.getExamDate().toString() : null,
                saveGroup.getGroupCode(),
                saveGroup.getImageNum()
        );
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
}
