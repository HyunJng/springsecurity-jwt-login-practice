package com.teamo.teamo.service;

import com.teamo.teamo.domain.User;
import com.teamo.teamo.repository.UserRepository;
import com.teamo.teamo.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("email이 같은 유저가 존재하는지 확인")
    public void existByEmail() throws Exception {
        // given
        User user = User.builder()
                .email("admin@naver.com")
                .role(Role.USER)
                .name("admin")
                .build();
        userRepository.save(user);

        // when
        boolean result = userService.existByEmail(user.getEmail());
        // then

        assertThat(result).isEqualTo(true);
    }
}