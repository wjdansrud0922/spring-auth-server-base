package com.wjdansrud.springauthserverbase.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    INVALID_EMAIL_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 인증 코드입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
