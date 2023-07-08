package com.teamo.teamo.security;

import com.teamo.teamo.domain.User;
import com.teamo.teamo.dto.OAuthAttributes;
import com.teamo.teamo.dto.SessionUser;
import com.teamo.teamo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
//@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    /**
     * Session 기반
     * - 소셜 서비스 서버(리소스 서버)에서 사용자 정보 가져온 상태에서
     *  추가로 진행하려는 Service
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("userRequest.accessToken = {}", userRequest.getAccessToken());
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 현재 로그인 중인 서비스 구분 코드(네이버, 구글 등 병행할 때 필요)
        String userNameAttributeName = userRequest.getClientRegistration()// OAuth2 로그인 진행 시 키가 되는 필드 값(~= PK 필드), 구글 기본지원(sub), 나머지 지원X
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        log.info("registrationId = {}, userNameAttributeName={}", registrationId, userNameAttributeName);

        OAuthAttributes attributes = OAuthAttributes.of( // Oauth2UserService를 통해 가져온 필요한 정보를 하나로 담기 위한 객체
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes()
        );
        User user = saveOrUpdate(attributes); // 사용자의 정보가 변할 수도 있어서 update도 되도록 구현
        httpSession.setAttribute("user", new SessionUser(user)); // 세션에 사용자 정보 저장

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(
                        user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName()))
                .orElse(attributes.toEntity()); // 없으면 User 생성

        return userRepository.save(user);
    }
}
