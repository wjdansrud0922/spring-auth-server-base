package com.wjdansrud.springauthserverbase.auth.config.jwt;



import com.wjdansrud.springauthserverbase.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();//요청 경로 추출하기
        log.info("요청 URI: {}", uri);
        List<String> whitelist = List.of(
                "/api/auth/signup/email/send",
                "/api/auth/signup/code/verify",
                "/api/auth/signup",
                "/api/auth/signin",
                "/api/auth/refresh"
        );

        if (whitelist.contains(uri)) {
            chain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");

        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7); // "Bearer " 이후부터 잘라서 토큰만 추출
        }

        if (jwtService.validateAccessToken(accessToken)) { //엑세스 토큰 검증
            SecurityContextHolder.getContext().setAuthentication( // 엑세스 토큰으로 인증 객체 설정
                    jwtService.getAccessAuthentication(accessToken)
            );
            log.info("엑세스 토큰 검증 성공, 인증 객체 설정 완료");
            chain.doFilter(request, response);
            return;
        }

        log.info("엑세스 토큰 만료됨, 리프레시 토큰 검증 시작");

        chain.doFilter(request, response);
    }
}

