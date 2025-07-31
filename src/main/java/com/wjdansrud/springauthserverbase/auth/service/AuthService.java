package com.wjdansrud.springauthserverbase.auth.service;

import com.wjdansrud.springauthserverbase.auth.config.jwt.JwtService;
import com.wjdansrud.springauthserverbase.auth.dto.req.CodeRequest;
import com.wjdansrud.springauthserverbase.auth.dto.req.RefreshToken;
import com.wjdansrud.springauthserverbase.auth.dto.req.SigninRequest;
import com.wjdansrud.springauthserverbase.auth.dto.req.SignupRequest;
import com.wjdansrud.springauthserverbase.auth.dto.res.TokenPair;
import com.wjdansrud.springauthserverbase.auth.util.CodeGenerator;
import com.wjdansrud.springauthserverbase.common.code.AuthErrorCode;
import com.wjdansrud.springauthserverbase.common.exception.AuthException;
import com.wjdansrud.springauthserverbase.redis.RedisService;
import com.wjdansrud.springauthserverbase.user.UserRepository;
import com.wjdansrud.springauthserverbase.user.entity.Role;
import com.wjdansrud.springauthserverbase.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodeGenerator codeGenerator;
    private final MailService mailService;
    private final RedisService redisService;

    @Value("${spring.data.redis.key.REDIS_EMAIL_CODE_BASE}")
    private String REDIS_EMAIL_CODE_BASE;

    @Value("${spring.data.redis.key.REDIS_EMAIL_VERIFICATION_BASE}")
    private String REDIS_EMAIL_VERIFICATION_BASE;

    public void sendEmailCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AuthException(AuthErrorCode.EMAIL_DUPLICATED);
        }

        String code = codeGenerator.generateCode(); //코드 생성
        mailService.sendMimeMessage(email, code);

        String key = REDIS_EMAIL_CODE_BASE + ":" + email; //레디스 키 생성
        redisService.save(key ,code, 5 * 60);
    }

    public void verifyEmailCode(CodeRequest codeRequest) {
        String Codekey = REDIS_EMAIL_CODE_BASE + ":" + codeRequest.getEmail(); //레디스 키 생성
        String savedCode = redisService.get(Codekey, String.class); //레디스에서 코드 조회

        if (savedCode == null || !savedCode.equals(codeRequest.getCode())) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL_CODE);
        }

        redisService.delete(Codekey);

        String emailKey = REDIS_EMAIL_VERIFICATION_BASE + ":" + codeRequest.getEmail();
        redisService.save(emailKey, codeRequest.getEmail(), 60 * 30);
    }


    
    @Transactional
    public void signup(SignupRequest req) {

        String emailKey = REDIS_EMAIL_VERIFICATION_BASE + ":" + req.getEmail();

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new AuthException(AuthErrorCode.EMAIL_DUPLICATED);
        }

        if (redisService.get(emailKey, String.class) == null) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword())) //비밀번호 암호화
                .role(Role.USER) //기본 역할은 USER로 설정
                .build();

        userRepository.save(user);
        redisService.delete(emailKey);
    }

    public TokenPair signin(SigninRequest req) {
        User u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new AuthException(AuthErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = jwtService.generateAccessToken(u); //엑세스 토큰 발급
        String refreshToken = jwtService.generateRefreshToken(u); //리프레쉬 토큰 발급(발급 시 자동 레디스 저장) - RTR(Rotate-Refresh-Token) 이라고 함


        return new TokenPair(accessToken, refreshToken);
    }
    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰과 리프레시 토큰을 포함하는 TokenPair 객체
     */
    public TokenPair refresh(RefreshToken refreshToken) {
        String id = jwtService.getRefreshAuthentication(refreshToken.getRefreshToken()).getName(); //리프레시 토큰으로 인증 객체에서 id 추출 (getName()이 id 반환함)
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND)); //리프레시 토큰으로 인증 객체에서 id 추출 후 사용자 조회


        if (jwtService.validateRefreshToken(refreshToken.getRefreshToken(), user.getEmail())) { //리프레시 토큰 검증
            String newAccessToken = jwtService.generateAccessToken(user);// 새로운 엑세스 토큰 발급

            String newRefreshToken = jwtService.generateRefreshToken(user); //새로운 리프레쉬 토큰 발급(발급 시 자동 레디스 저장) - RTR(Rotate-Refresh-Token) 이라고 함


            return new TokenPair(newAccessToken, newRefreshToken);
        } else {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID); //리프레시 토큰이 유효하지 않은 경우 예외 발생
        }


    }

    public void logout(User user) {
        jwtService.logout(user);
    }
}
