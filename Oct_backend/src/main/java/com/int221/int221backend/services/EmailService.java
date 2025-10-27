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

    @Value("${app.frontend.url}")
    private String appFrontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(Users user, String jwtToken) { // เปลี่ยนชื่อตัวแปรเป็น jwtToken เพื่อความชัดเจน

        // สร้าง verification URL สำหรับ frontend โดยใส่ JWT Token เป็น Query Parameter
        // สมมติว่า Frontend URL ชี้ไปที่หน้าที่จะจัดการ Token: e.g., http://frontend.com/verify?token={JWT}
        String verificationUrl = appFrontendUrl + "/verify-email?token=" + jwtToken;

        String subject = "Please verify your email";
        String content = "Hello " + user.getFullName() + ",\n\n"
                + "Thank you for registering. Please click the link below to verify your email:\n"
                + verificationUrl + "\n\n" // ใช้ verificationUrl ที่มี JWT
                + "If you did not register, please ignore this email.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(fromEmail);

        mailSender.send(message);
    }
}
