package com.teamo.teamo.controller;

import com.teamo.teamo.dto.PostSigninAutoRes;
import com.teamo.teamo.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/oauth2/success")
    public BaseResponse<PostSigninAutoRes> loginSuccess(@RequestParam("accessToken") String accessToken, @RequestParam("refreshToken") String refreshToken) {
        PostSigninAutoRes postLoginRes = new PostSigninAutoRes(accessToken, refreshToken);
        return new BaseResponse<>(postLoginRes);
    }

}
