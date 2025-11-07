package com.likelion.server.domain.user.repository;

import com.likelion.server.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 레포지토리
 * - 닉네임 중복 확인 및 조회
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNicknameAndPassword(
            @NotBlank(message = "닉네임은 필수 입력 값입니다.")
            String nickname,

            @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
            String password
    );

    Optional<User> findByNickname(
            @NotBlank(message = "닉네임은 필수 입력 값입니다.")
            String nickname
    );

    boolean existsByNickname(
            @NotBlank(message = "닉네임은 필수 입력 값입니다.")
            String nickname
    );

    Optional<User> findByRefreshToken(
            String refreshToken
    );
}
