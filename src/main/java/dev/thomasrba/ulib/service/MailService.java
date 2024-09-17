package dev.thomasrba.ulib.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService {

    JavaMailSender mailSender;

    public void sendCode(int code, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@thomasrba.dev");
        message.setTo(email);
        message.setSubject("Your activation code !");
        message.setText("Your activation code : " + code);
        mailSender.send(message);
    }
}
