package com.teamo.teamo.security.handler;

import com.teamo.teamo.domain.PrincipalDetails;
import com.teamo.teamo.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@AllArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
//    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.createAccessToken(oAuth2User.getUser().getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(oAuth2User.getUser().getId());

        // TODO: refresh Token 저장하기
//        authService
        String targetUrl = UriComponentsBuilder.fromUriString("/auth/oauth2/success")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
