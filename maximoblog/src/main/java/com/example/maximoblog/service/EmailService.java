package com.example.maximoblog.service;

import com.example.maximoblog.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ── Verification Email ──────────────────────────────────────

    public void sendVerificationEmail(User user, String token) {
        String verifyUrl = baseUrl + "/auth/verify?token=" + token;
        String subject = "MaximoBlog - Verify Your Email";
        String body = buildVerificationHtml(user.getName(), verifyUrl);
        sendHtmlEmail(user.getEmail(), subject, body);
    }

    // ── Password Reset Email ────────────────────────────────────

    public void sendPasswordResetEmail(User user, String token) {
        String resetUrl = baseUrl + "/auth/reset-password?token=" + token;
        String subject = "MaximoBlog - Reset Your Password";
        String body = buildPasswordResetHtml(user.getName(), resetUrl);
        sendHtmlEmail(user.getEmail(), subject, body);
    }

    // ── HTML Builders ───────────────────────────────────────────

    private String buildVerificationHtml(String name, String url) {
        return "<html>"
                + "<body style='font-family: Segoe UI, Arial, sans-serif; background: #f4f4f7; padding: 40px 0;'>"
                + "<div style='max-width: 520px; margin: auto; background: #fff; border-radius: 12px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);'>"
                + "<h2 style='color: #1a1a2e; margin-bottom: 8px;'>Welcome to MaximoBlog!</h2>"
                + "<p style='color: #555; font-size: 15px;'>Hi <strong>" + name + "</strong>,</p>"
                + "<p style='color: #555; font-size: 15px;'>Please verify your email address by clicking the button below:</p>"
                + "<div style='text-align: center; margin: 32px 0;'>"
                + "<a href='" + url + "' style='background: #6c63ff; color: #fff; padding: 14px 36px; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 15px;'>Verify Email</a>"
                + "</div>"
                + "<p style='color: #999; font-size: 13px;'>This link expires in <strong>24 hours</strong>.</p>"
                + "<p style='color: #999; font-size: 13px;'>If you did not create an account, you can ignore this email.</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    private String buildPasswordResetHtml(String name, String url) {
        return "<html>"
                + "<body style='font-family: Segoe UI, Arial, sans-serif; background: #f4f4f7; padding: 40px 0;'>"
                + "<div style='max-width: 520px; margin: auto; background: #fff; border-radius: 12px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);'>"
                + "<h2 style='color: #1a1a2e; margin-bottom: 8px;'>Password Reset</h2>"
                + "<p style='color: #555; font-size: 15px;'>Hi <strong>" + name + "</strong>,</p>"
                + "<p style='color: #555; font-size: 15px;'>We received a request to reset your password. Click the button below:</p>"
                + "<div style='text-align: center; margin: 32px 0;'>"
                + "<a href='" + url + "' style='background: #e63946; color: #fff; padding: 14px 36px; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 15px;'>Reset Password</a>"
                + "</div>"
                + "<p style='color: #999; font-size: 13px;'>This link expires in <strong>15 minutes</strong>.</p>"
                + "<p style='color: #999; font-size: 13px;'>If you did not request this, you can safely ignore this email.</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    // ── Internal ────────────────────────────────────────────────

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
