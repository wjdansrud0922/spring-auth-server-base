package com.wjdansrud.springauthserverbase.common.exception;

import com.wjdansrud.springauthserverbase.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final ErrorCode errorCode;

    public UserException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
