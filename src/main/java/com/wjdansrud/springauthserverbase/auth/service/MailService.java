package com.wjdansrud.springauthserverbase.auth.service;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendMimeMessage(String email, String code) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 메일을 받을 수신자 설정
            mimeMessageHelper.setTo(email);
            // 메일의 제목 설정
            mimeMessageHelper.setSubject("Whisepr Auth Code");

            // html 문법 적용한 메일의 내용
            String content = String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <style>
                            .container {
                                max-width: 600px;
                                margin: 0 auto;
                                padding: 20px;
                                font-family: 'Arial', sans-serif;
                            }
                            .logo {
                                text-align: center;
                                margin-bottom: 30px;
                                color: #6B4EFF;
                                font-size: 32px;
                                font-weight: bold;
                            }
                            .code-box {
                                background-color: #F5F5F5;
                                border-radius: 8px;
                                padding: 20px;
                                text-align: center;
                                margin: 20px 0;
                            }
                            .verification-code {
                                font-size: 28px;
                                font-weight: bold;
                                color: #6B4EFF;
                                letter-spacing: 4px;
                            }
                            .message {
                                color: #666666;
                                line-height: 1.6;
                                margin: 20px 0;
                            }
                            .footer {
                                font-size: 12px;
                                color: #999999;
                                text-align: center;
                                margin-top: 30px;
                                border-top: 1px solid #EEEEEE;
                                padding-top: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="logo">
                                Whisper
                            </div>
                    
                            <div class="message">
                                안녕하세요!<br>
                                Whisper 회원가입을 위한 인증 코드입니다.
                            </div>
                    
                            <div class="code-box">
                                <span class="verification-code">%s</span>
                            </div>
                    
                            <div class="message">
                                인증 코드는 5분 동안만 유효합니다.<br>
                                본인이 요청하지 않은 경우 이 메일을 무시하셔도 됩니다.
                            </div>
                    
                            <div class="footer">
                                © 2025 Whisper. All rights reserved.<br>
                                본 메일은 발신전용입니다.
                            </div>
                        </div>
                    </body>
                    </html>
                    """, code);

            // 메일의 내용 설정
            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessage);

            log.info("메일 발송 성공!");
        } catch (Exception e) {
            log.info("메일 발송 실패!");
            throw new RuntimeException(e);
        }
    }

}