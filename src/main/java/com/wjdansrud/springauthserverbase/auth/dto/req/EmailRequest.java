package com.wjdansrud.springauthserverbase.auth.dto.req;

import lombok.Getter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailRequest {
    @NotBlank
    @Email
    private String email;
}