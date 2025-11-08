package com.likelion.server.domain.group.repository;

import com.likelion.server.domain.group.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<Member, Long> {}
