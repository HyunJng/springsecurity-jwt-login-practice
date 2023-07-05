package com.teamo.teamo.config;

import com.teamo.teamo.security.CustomOAuth2UserService;
import com.teamo.teamo.type.Role;
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

    private final CustomOAuth2UserService customOAuth2UserService;

    private static final String[] WHITE_LIST = {
            "/",
            "/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions((frameOptionsConfig -> frameOptionsConfig.disable()))
                )
                .authorizeHttpRequests(authorize -> {
                            try {
                                authorize
                                        .requestMatchers(WHITE_LIST)
                                        .permitAll()// 전체권한
                                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                                        .requestMatchers("/api/v1/**")
                                        .hasRole(Role.USER.name()); // USER 만
//                                        .anyRequest()
//                                        .authenticated(); // 나머지는 모두 인증된 사용자들에게만 허용
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 "/" 로 이동
                )
                .oauth2Login(oauth2 -> oauth2 
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig // 로그인 성공 이후 사용자 정보 가져올 때의 설정
                                .userService(customOAuth2UserService) // 소셜 로그인 성공 시 후속조치 할 서비스 구현체 명시
                        )
                );
//                .csrf().disable()
//                .headers().frameOptions().disable() // H2 Console쓰려고
//                .and()
//                .authorizeHttpRequests()
//                .requestMatchers("/", "/css/**", "/images/**","/js/**", "/h20console/**")
//                .permitAll()
//                .requestMatchers("/api/v1/**")
//                .hasRole(Role.USER.name())
//                .anyRequest().authenticated()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/")
//                .and()
//                .oauth2Login()
//                .userInfoEndpoint()
//                .userService();

        return http.build();
    }
}
