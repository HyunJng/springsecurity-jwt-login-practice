package com.teamo.teamo.filter;

import com.teamo.teamo.security.AuthConstants;
import com.teamo.teamo.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * JWT를 검증하는 필터
     * HttpServletRequest 의 Authorization 헤더에서 JWT token을 찾고 그것이 맞는지 확인
     * UsernamePasswordAuthenticationFilter 앞에서 작동
     * (JwtTokenFilterConfigurer 참고)
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String jwt = resolveToken(request, AuthConstants.AUTH_HEADER);
        String jwt = String.valueOf(request.getHeader(AuthConstants.AUTH_HEADER))
                .replace(AuthConstants.TOKEN_TYPE, "");
        try {
            if (Objects.nonNull(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("권한 부여됨 for '{}', uri: {}", authentication.getName(), request.getRequestURI());
            } else {
                throw new JwtException("Token is null");
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", e);
            log.info("ExpiredJwtException: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            request.setAttribute("exception", e);
            log.info("jwtException: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // 제대로 된 토큰 형식인지 확인 & 앞 글자 제외
    private String resolveToken(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith("Bearer-")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
