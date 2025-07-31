package com.wjdansrud.springauthserverbase.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

;

    private final HttpStatus httpStatus;
    private final String message;
}
