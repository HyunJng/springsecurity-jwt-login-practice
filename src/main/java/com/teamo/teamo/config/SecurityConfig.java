package com.teamo.teamo.config;

import com.teamo.teamo.security.JwtTokenProvider;
import com.teamo.teamo.security.PrincipalOAuth2DetailsService;
import com.teamo.teamo.security.handler.JwtAccessDeniedHandler;
import com.teamo.teamo.security.handler.JwtAuthenticationEntryPoint;
import com.teamo.teamo.security.handler.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalOAuth2DetailsService principalOAuth2DetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(basic -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions((frameOptionsConfig -> frameOptionsConfig.disable()))
                )
                .authorizeHttpRequests(authorize -> {
                            try {
                                authorize
                                        .requestMatchers(PathRequest.toH2Console())
                                        .permitAll()
                                        .requestMatchers("/api/**").permitAll()
                                        .requestMatchers("/oauth2/authorization/google/**").permitAll()
                                        .requestMatchers("/auth/**").authenticated()
                                        .anyRequest().permitAll();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .exceptionHandling(except -> {
                    except.authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패시 오류 처리
                            .accessDeniedHandler(jwtAccessDeniedHandler); // 권한 부족 시 오류 처리
                })
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig // 로그인 성공 이후 사용자 정보 가져올 때의 설정
                                                .userService(principalOAuth2DetailsService)

                                ).successHandler(new OAuth2SuccessHandler(jwtTokenProvider))
                )
                .apply(new JwtTokenFilterConfigurer(jwtTokenProvider)); // JWT 검증 필터

        return http.build();
    }

}
