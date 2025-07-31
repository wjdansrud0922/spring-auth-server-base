package com.wjdansrud.springauthserverbase.auth.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SigninRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
