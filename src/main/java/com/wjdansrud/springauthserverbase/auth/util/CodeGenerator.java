package com.wjdansrud.springauthserverbase.auth.util;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator  {

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(CHAR_POOL.length());
            code.append(CHAR_POOL.charAt(index));
        }
        return code.toString();
    }
}