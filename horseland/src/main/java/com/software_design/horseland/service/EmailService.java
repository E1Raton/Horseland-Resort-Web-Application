package com.software_design.horseland.service;

import com.software_design.horseland.annotation.Auditable;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private JavaMailSender mailSender;

    @Auditable(operation = "SEND EMAIL", username = "#toEmail")
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Horseland Password Reset Verification");
        message.setText("Your password reset code is: " + code);

        mailSender.send(message);
    }
}
