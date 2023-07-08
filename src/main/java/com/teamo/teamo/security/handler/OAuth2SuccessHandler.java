package com.teamo.teamo.security.handler;

import com.teamo.teamo.domain.PrincipalDetails;
import com.teamo.teamo.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();

        log.info("oAuth2User.getUser().getEmail = {} & getId = {}", oAuth2User.getUser().getEmail(), oAuth2User.getUser().getName());
        // refresh token 발급 및 저장(미룸)
        String refreshToken = jwtTokenProvider.createRefreshToken(oAuth2User.getUser().getEmail());

        // accessToken과 refreshToken 리턴
        String targetUrl = UriComponentsBuilder.fromUriString("/auth/oauth2/success") // 성공하면 이동
                .queryParam("accessToken", jwtTokenProvider.createAccessToken(oAuth2User.getUser().getEmail()))
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
