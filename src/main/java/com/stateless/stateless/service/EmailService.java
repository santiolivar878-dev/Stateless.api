package com.stateless.stateless.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String url, String name) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("🔑 Recupera tu contraseña — STATELESS");
        
        // Simulación del componente Mailable de Laravel
        String htmlContent = "<h1>Hola " + name + "</h1>" +
                "<p>Recibimos una solicitud para restablecer tu contraseña.</p>" +
                "<a href='" + url + "' style='background:#000; color:#fff; padding:10px 20px; text-decoration:none;'>Restablecer contraseña</a>" +
                "<p>Este enlace expira en 1 hora.</p>";

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}