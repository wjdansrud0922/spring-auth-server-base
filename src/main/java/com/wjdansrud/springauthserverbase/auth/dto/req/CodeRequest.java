package com.wjdansrud.springauthserverbase.auth.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CodeRequest {
    @Email
    @NotBlank
    private String email;

    @Size(min = 6, max = 6)
    @NotBlank
    private String code;
}
