package com.teamo.teamo.security.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Token {

    private String accessToken;
    private String refreshToken;

    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
