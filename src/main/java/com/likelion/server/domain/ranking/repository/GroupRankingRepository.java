package com.likelion.server.domain.ranking.repository;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.ranking.entity.GroupRanking;
import com.likelion.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRankingRepository extends JpaRepository<GroupRanking, Long> {
    // 그룹 전체 랭킹을 정답순으로 정렬
    List<GroupRanking> findAllByGroupOrderByTotalScoreDesc(Group group);

    // 그룹 내 나의 랭킹 조회
    Optional<GroupRanking> findByGroupAndUser(Group group, User user);

    // 특정 유저 랭킹 삭제 (그룹 퇴장)
    void deleteAllByGroup(Group group);

    // 그룹의 모든 랭킹 삭제 (그룹 삭제)
    void deleteByUserAndGroup(User user, Group group);
}
