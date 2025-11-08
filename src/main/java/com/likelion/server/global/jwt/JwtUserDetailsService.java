package com.likelion.server.global.jwt;

import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * JWT 인증용 사용자 정보 로드 서비스
 */
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + nickname));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getNickname())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}
