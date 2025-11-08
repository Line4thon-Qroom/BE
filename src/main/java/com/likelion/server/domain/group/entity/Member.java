package com.likelion.server.domain.group.entity;

import com.likelion.server.domain.group.entity.enums.Role;
import com.likelion.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member", uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Role role;


    public Member(Group groupId, User userId, Role role) {
        this.group = groupId;
        this.user = userId;
        this.role = role;
    }
}
