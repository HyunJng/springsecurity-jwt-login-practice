package com.teamo.teamo.security;

import com.teamo.teamo.service.MyUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Getter
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final MyUserDetailsService myUserDetailsService;

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
    public String createAccessToken(String userEmail) {
        log.info("createAccessToken 시작");
        // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        Claims claims = Jwts.claims().setSubject(userEmail);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(new Date(now.getTime() + JWTConstants.ACCESS_TOKEN_EXPIRED)) // 만료일
                .signWith(key, SignatureAlgorithm.HS256)// 사용하라 알고리즘 & signature
                .compact();
    }


    public String createRefreshToken(String userEmail) {
        log.info("createRefreshToken 시작");
        Claims claims = Jwts.claims().setSubject(userEmail);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + JWTConstants.REFRESH_TOKEN_EXPIRED))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // accessToken 남은 유효시간
    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }


    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            log.info("claims = {}", claims.getSubject());
            return true;
        } catch (ExpiredJwtException e) {
            // 만료된 경우에는 refresh token을 확인하기 위해
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * 토큰으로 부터 Authentication 객체를 얻어온다.
     * Authentication 안에 user의 정보가 담겨있음.
     * UsernamePasswordAuthenticationToken 객체로 Authentication을 쉽게 만들수 있으며,
     * 매게변수로 UserDetails, pw, authorities 까지 넣어주면
     * setAuthenticated(true)로 인스턴스를 생성해주고
     * Spring-Security는 그것을 체크해서 로그인을 처리함
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // userEmail, Password, Role을 이용해 만든 userDetails
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }
}
