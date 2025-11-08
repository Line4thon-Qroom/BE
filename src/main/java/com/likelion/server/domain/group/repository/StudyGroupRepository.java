package com.likelion.server.domain.group.repository;

import com.likelion.server.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupRepository extends JpaRepository<Group, Long> {
    boolean existsByGroupCode(String groupCode);
}