package com.wjdansrud.springauthserverbase.auth.dto.res;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenPair {
    private String type;
    private String accessToken;
    private String refreshToken;

    public TokenPair(String accessToken, String refreshToken) {
        this.type = "Bearer";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}

