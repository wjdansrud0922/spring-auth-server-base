package com.wjdansrud.springauthserverbase.auth.controller;

import com.wjdansrud.springauthserverbase.auth.config.UserPrincipal;
import com.wjdansrud.springauthserverbase.auth.config.jwt.JwtService;
import com.wjdansrud.springauthserverbase.auth.dto.req.*;
import com.wjdansrud.springauthserverbase.auth.dto.res.TokenPair;
import com.wjdansrud.springauthserverbase.auth.service.AuthService;
import com.wjdansrud.springauthserverbase.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup/email/send")
    public ResponseEntity<SuccessResponse<?>> sendEmailCode(@RequestBody @Valid EmailRequest req) {
        authService.sendEmailCode(req.getEmail());

        return ResponseEntity.ok(new SuccessResponse<>("이메일로 인증 코드 발송 성공", null));
    }

    @PostMapping("/signup/code/verify")
    public ResponseEntity<SuccessResponse<?>> verifyEmailCode(@RequestBody @Valid CodeRequest req) {
        authService.verifyEmailCode(req);

        return ResponseEntity.ok(new SuccessResponse<>("이메일 인증 성공", null));
    }

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<?>> signup(@RequestBody @Valid SignupRequest req)
    {
        authService.signup(req);

        return ResponseEntity.ok(new SuccessResponse<>("회원가입 성공", null));
    }

    @PostMapping("/signin")
    public ResponseEntity<SuccessResponse<TokenPair>> signin(@RequestBody @Valid SigninRequest req) {
        TokenPair res = authService.signin(req);

        SecurityContextHolder.getContext().setAuthentication(
                jwtService.getAccessAuthentication(res.getAccessToken())
        );

        return ResponseEntity.ok(new SuccessResponse<>("로그인 성공", res));
    }

    /**
     * 로그아웃 API
     * @param userPrincipal 현재 인증된 사용자 정보
     * @return 로그아웃 성공 메시지를 포함한 성공 응답
     */

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<?>> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        authService.logout(userPrincipal.getUser());

        return ResponseEntity.ok(new SuccessResponse<>("로그아웃 성공", null));

    }

    /**
     * 토큰 갱신 API
     * @param refreshToken 요청 본문에 포함된 RefreshToken 객체
     * @return 갱신된 토큰 쌍을 포함한 성공 응답
     */
    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse<TokenPair>> refresh(@RequestBody @Valid RefreshToken refreshToken) {
        TokenPair res = authService.refresh(refreshToken);


        SecurityContextHolder.getContext().setAuthentication(
                jwtService.getAccessAuthentication(res.getAccessToken())
        );

        return ResponseEntity.ok(new SuccessResponse<>("토큰 갱신 성공", res));
    }



}
