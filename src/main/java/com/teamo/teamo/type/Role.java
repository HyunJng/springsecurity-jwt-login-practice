package com.teamo.teamo.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GEUST("ROLE_GUEST", "손님"), // SpringSecurity는 권한의 앞에 ROLE이 붙어있어야한다.
    USER("ROLE_USER", "일반사용자");

    private final String key;
    private final String title;
}
