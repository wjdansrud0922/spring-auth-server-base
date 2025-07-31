package com.wjdansrud.springauthserverbase.auth.dto.req;


import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class SignupRequest {
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username; // 사용자 이름

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password; // 비밀번호
}