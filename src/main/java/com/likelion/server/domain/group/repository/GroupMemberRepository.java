package com.likelion.server.domain.group.repository;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.group.entity.Member;
import com.likelion.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<Member, Long> {
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    List<Member> findAllByUser(User user);

    Integer countByGroup(Group group);

    Optional<Member> findByUserAndGroup(User user, Group group);
}
