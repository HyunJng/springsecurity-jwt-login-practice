package com.teamo.teamo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Getter
@Component
public class JwtTokenProvider {

    @Value("${jwt.token.key}")
    private String secretKey;
    private Key key;

    @PostConstruct
    private void initialize() {
        // key 인코딩
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key = Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    // JWT 생성
    public String createAccessToken(Long userId) {
        log.info("createAccessToken 시작");
        // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        Claims claims = Jwts.claims().setSubject(userId.toString());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(new Date(now.getTime() + JWTConstants.ACCESS_TOKEN_EXPIRED)) // 만료일
                .signWith(key, SignatureAlgorithm.HS256)// 사용하라 알고리즘 & signature
                .compact();
    }


    public String createRefreshToken(Long userId) {
        log.info("createRefreshToken 시작");
        Claims claims = Jwts.claims().setSubject(userId.toString());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + JWTConstants.REFRESH_TOKEN_EXPIRED))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserIdFromRef(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // accessToken 남은 유효시간
    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }


}
