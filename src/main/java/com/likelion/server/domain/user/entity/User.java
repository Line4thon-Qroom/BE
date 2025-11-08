package com.likelion.server.domain.user.entity;

import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 * - 닉네임, 비밀번호, 생성일시
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;  // 닉네임 (로그인 ID 역할)

    @Column(nullable = false, length = 255)
    private String password;  // 암호화된 비밀번호

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Builder
    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
