package com.stateless.stateless.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired private JavaMailSender mailSender;
    @Autowired private TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String emailRemitente;

    public void sendVerificationEmail(String to, String url, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("url", url);
        context.setVariable("nombre", name);
        String html = templateEngine.process("emails/verificar-email", context);
        sendHtmlEmail(to, "✅ Verifica tu cuenta", html);
    }

    public void sendResetPasswordEmail(String to, String url, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("url", url);
        context.setVariable("nombre", name);
        String html = templateEngine.process("emails/recuperar-password", context);
        sendHtmlEmail(to, "🔑 Recupera tu contraseña", html);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(emailRemitente); // OBLIGATORIO PARA GMAIL
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
}