package com.likelion.server.domain.group.repository;

import com.likelion.server.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyGroupRepository extends JpaRepository<Group, Long> {
    boolean existsByGroupCode(String groupCode);
    Group findByGroupCode(String groupCode);
}