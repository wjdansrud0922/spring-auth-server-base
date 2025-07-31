package com.wjdansrud.springauthserverbase.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TokenPair {
    private String accessToken;
    private String refreshToken;

}
