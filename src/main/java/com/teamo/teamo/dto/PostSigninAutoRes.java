package com.teamo.teamo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSigninAutoRes {
    private String accessToken;
    private String refreshToken;
}
