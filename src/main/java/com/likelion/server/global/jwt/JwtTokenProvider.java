package com.likelion.server.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성 / 검증 유틸리티 (parserBuilder 없는 버전)
 */
@Component
public class JwtTokenProvider {

    // 비밀키 (256bit 이상 권장)
    private static final String SECRET_KEY = "QROOM_SECRET_KEY_QROOM_SECRET_KEY_1234567891011";
    // 토큰 유효기간
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60;       // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /** 1. 토큰 생성 */
    /** Access Token */
    public String createAccessToken(String nickname) {
        Date now = new Date();
        return Jwts.builder()
                .subject(nickname)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    /** Refresh Token */
    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    /** 2. 토큰에서 닉네임(subject) 추출 */
    public String getNicknameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /** 3. 토큰 유효성 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("❌ JWT 만료됨");
        } catch (Exception e) {
            System.out.println("❌ JWT 검증 실패: " + e.getMessage());
        }
        return false;
    }
}
