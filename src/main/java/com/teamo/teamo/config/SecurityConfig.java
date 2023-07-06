package com.teamo.teamo.config;

import com.teamo.teamo.security.CustomOAuth2UserService;
import com.teamo.teamo.security.JwtTokenProvider;
import com.teamo.teamo.security.PrincipalOAuth2DetailsService;
import com.teamo.teamo.security.handler.OAuth2SuccessHandler;
import com.teamo.teamo.service.AuthService;
import com.teamo.teamo.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalOAuth2DetailsService principalOAuth2DetailsService;
//    private final CustomOAuth2UserService customOAuth2UserService; // 책

    private static final String[] WHITE_LIST = {
            "/api/login",
            "/api/main"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(basic -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions((frameOptionsConfig -> frameOptionsConfig.disable()))
                )
//                .authorizeHttpRequests(authorize -> { // 책
//                            try {
//                                authorize
//                                        .requestMatchers(WHITE_LIST)
//                                        .permitAll()// 전체권한
//                                        .requestMatchers(PathRequest.toH2Console()).permitAll()
//                                        .requestMatchers("/api/admin")
//                                        .hasRole(Role.ADMIN.name()); // GEUST 만
////                                        .anyRequest()
////                                        .authenticated(); // 나머지는 모두 인증된 사용자들에게만 허용
//                            } catch (Exception e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                )
//                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
//                        .logoutSuccessUrl("/api/") // 로그아웃 성공 시 "/" 로 이동
//                )
                .oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig // 로그인 성공 이후 사용자 정보 가져올 때의 설정
//                                .userService(customOAuth2UserService) // 소셜 로그인 성공 시 후속조치 할 서비스 구현체 명시
                                                .userService(principalOAuth2DetailsService)

                                ).successHandler(new OAuth2SuccessHandler(jwtTokenProvider))
                );

        return http.build();
    }

}
