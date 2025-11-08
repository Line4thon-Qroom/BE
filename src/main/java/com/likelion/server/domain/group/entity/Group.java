package com.likelion.server.domain.group.entity;

import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "study_group")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(unique = true, length = 6)
    private String groupCode;   // 초대 코드

    private String examDate;    // 예정 시험일 (문자열로 관리 가능)

    @Column(nullable = false)
    private Integer imageNum;

}
