package com.teamo.teamo.service;

import com.teamo.teamo.domain.User;
import com.teamo.teamo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    // TODO :Exception 제대로 만들기
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 없음"));
    }
}
