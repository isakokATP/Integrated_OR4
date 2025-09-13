package com.int221.int221backend.services;

import com.int221.int221backend.entities.Users;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(Users user, String token) {
        try {
            String toEmail = user.getEmail();
            String username = user.getNickName();
            String verificationUrl = "http://intproj24.sit.kmutt.ac.th/or4/verify-email/?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Please verify your email");
            helper.setText(
                    "<p>Hi " + username + ",</p>" +
                            "<p>Please verify your email by clicking the link below:</p>" +
                            "<a href=\"" + verificationUrl + "\">Verify Email</a>",
                    true // true = HTML
            );

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        }
    }
}
