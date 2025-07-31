package com.wjdansrud.springauthserverbase.auth.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendMimeMessage(String email, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[Spring Auth Base]인증 코드 안내");

            String content = String.format("""
                    안녕하세요.

                    아래 인증 코드를 입력해주세요:

                    [ %s ]

                    해당 코드는 5분간 유효합니다.
                    본인이 요청하지 않았다면 이 메일은 무시해주세요.
                    """, code);

            helper.setText(content, false); // false: HTML 아님, 텍스트

            javaMailSender.send(message);
            log.info("인증 코드 메일 발송 성공: {}", email);
        } catch (Exception e) {
            log.error("인증 코드 메일 발송 실패: {}", email, e);
            throw new RuntimeException("인증 코드 메일 전송 중 오류 발생", e);
        }
    }
}
