package com.teamo.teamo.security;

import com.teamo.teamo.domain.PrincipalDetails;
import com.teamo.teamo.domain.User;
import com.teamo.teamo.service.UserService;
import com.teamo.teamo.type.Role;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalOAuth2DetailsService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    /**
     * 소셜 서비스 서버(리소스 서버)에서 사용자 정보 가져온 상태에서
     * 추가로 진행하려는 Service
     * loadUser() 이 해야하는 일
     * 1. access token을 이용해 서드파티 서버로부터 사용자 정보 받아오기
     * 2. 해당 사용자가 이미 회원가입 되어있는 사용자라면 정보 업데이트
     *                                 아니라면 회원가입 처리
     * 3. UserPrincipal을 return
     *      . 세션방식은 return한 객체가 security 세션에 저장
     *      . JWT방식에선 저장 X
     *  ===========
     *  앞서 CustomOAuth2UserService와 차이.
     *  - Custom은 DefaultOAuth를 따로 생성해서 1을 진행.
     *  - DefaultOAuth2UserService를 extends하는 클래스는 Custom에서 implements했던
     *    OAuth2UserService를 상속하고 있어서 이 방법이 더 나아 보인다.
     */

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 1번 해줌

        validationAttribute(oAuth2User.getAttributes());
        /**
         * oAuth2User.getAttributes() = {
         *          sub=112327663769392968411,
         *          picture=https://lh3.googleusercontent.com/a/default-user=s96-c,
         *          email=khjung1654@gmail.com,
         *          email_verified=true}
         */
        String email = oAuth2User.getAttribute("email").toString();
        log.info("email = {}", email);
        User user = User.builder()
                .email(email)
                .role(Role.USER)
                .name(email.split("@")[0])
                .password(passwordEncoder.encode(email)) // password는 email encode
                .provider(userRequest.getClientRegistration().getRegistrationId())
                .provider_id(oAuth2User.getAttribute("sub").toString())
                .build();
        try {
            if (!userService.existByEmail(user.getEmail())) {
                log.info("OAuth2 회원가입 시작");
                userService.createUser(user);
            } else {
                log.info("OAuth2 기존 회원 존재");
                user = userService.findByEmail(email);
            }
        } catch (Exception e) { // TODO: Exception 제대로 만들기
            throw new RuntimeException(e);
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    private void validationAttribute(Map<String, Object> attributes) {
        if (!attributes.containsKey("email")) {
            throw new IllegalArgumentException("응답에 email이 존재하지 않는다.");
        }
    }
}
