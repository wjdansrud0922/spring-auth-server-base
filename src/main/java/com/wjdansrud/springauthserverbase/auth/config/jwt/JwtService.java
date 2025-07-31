package com.wjdansrud.springauthserverbase.auth.config.jwt;


import com.wjdansrud.springauthserverbase.auth.config.CustomUserDetailsService;
import com.wjdansrud.springauthserverbase.redis.RedisService;
import com.wjdansrud.springauthserverbase.user.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;

@Service
public class JwtService {
    private final CustomUserDetailsService userDetailsService;
    private final JwtGenerator generator;
    private final JwtUtil util;
    private final RedisService redisService;

    private final Key ACCESS_KEY;
    private final Key REFRESH_KEY;
    private final long ACCESS_EXP;
    private final long REFRESH_EXP;
    private final String REDIS_REFRESH_KEY_BASE;

    // 생성자: 의존성 및 JWT 관련 설정값 주입
    public JwtService(
            CustomUserDetailsService userDetailsService,
            JwtGenerator jwtGenerator,
            JwtUtil jwtUtil,
            StringRedisTemplate redisTemplate,
            @Value("${spring.jwt.access-token.secret}") String accessSecret,
            @Value("${spring.jwt.refresh-token.secret}") String refreshSecret,
            @Value("${spring.jwt.access-token.expiration}") long accessExpiration,
            @Value("${spring.jwt.refresh-token.expiration}") long refreshExpiration,
            @Value("${spring.data.redis.key.REDIS_REFRESH_KEY_BASE}") String redisRefreshKeyBase,
            RedisService redisService) {
        this.userDetailsService = userDetailsService;
        this.generator = jwtGenerator;
        this.util = jwtUtil;
        REDIS_REFRESH_KEY_BASE = redisRefreshKeyBase;
        this.ACCESS_KEY = jwtUtil.getSigningKey(accessSecret);
        this.REFRESH_KEY = jwtUtil.getSigningKey(refreshSecret);
        this.ACCESS_EXP = accessExpiration;
        this.REFRESH_EXP = refreshExpiration;
        this.redisService = redisService;
    }

    // 1) Access Token 발급
    @Transactional
    public String generateAccessToken(User u) {
        String at = generator.generateAccessToken(ACCESS_KEY, ACCESS_EXP, u);
        return at;
    }

    // 2) Refresh Token 발급 + Redis 저장(RTR)
    @Transactional
    public String generateRefreshToken(User u) {
        String rt = generator.generateRefreshToken(REFRESH_KEY, REFRESH_EXP, u);


        // Redis에 저장
        String key = REDIS_REFRESH_KEY_BASE + ":" + u.getEmail();
        redisService.save(key, rt, REFRESH_EXP);

        return rt;
    }

    // 3) Access Token 검증
    public boolean validateAccessToken(String t) {
        boolean result = util.getTokenStatus(t, ACCESS_KEY) == TokenStatus.AUTHENTICATED;
        System.out.println("[JwtService] validateAccessToken: token=" + util.getTokenStatus(t, ACCESS_KEY) + ", result=" + result);
        return result;
    }

    // 4) Refresh Token 검증 (서명 + Redis 일치 여부)
    public boolean validateRefreshToken(String t, String email) {
        boolean ok = util.getTokenStatus(t, REFRESH_KEY) == TokenStatus.AUTHENTICATED;
        if (!ok) return false;

        String key = REDIS_REFRESH_KEY_BASE + ":" + email;
        String stored = redisService.get(key, String.class);
        return t.equals(stored);
    }

    // 5) Access Token 기반 Authentication 객체 생성
    public Authentication getAccessAuthentication(String token) {
        String email = Jwts.parserBuilder()
                .setSigningKey(ACCESS_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        UserDetails principal = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(
                principal, "", principal.getAuthorities());
    }

    // 5-2) Refresh Token 기반 Authentication 객체 생성
    public Authentication getRefreshAuthentication(String token) {
        String email = Jwts.parserBuilder()
                .setSigningKey(REFRESH_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        UserDetails principal = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(
                principal, "", principal.getAuthorities());
    }

    // 7) 로그아웃 처리: Redis 키 삭제 + 쿠키 만료
    @Transactional
    public void logout(User u) {
        String key = REDIS_REFRESH_KEY_BASE + ":" + u.getEmail();
        SecurityContextHolder.clearContext();
        redisService.delete(key);
    }

}
