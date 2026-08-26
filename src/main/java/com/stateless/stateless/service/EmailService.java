package com.stateless.stateless.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired private JavaMailSender mailSender;
    @Autowired private TemplateEngine templateEngine;

    private void sendHtmlEmail(String to, String subject, String templateName, Context context) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        String htmlContent = templateEngine.process(templateName, context);
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String url, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("url", url);
        context.setVariable("nombre", name);
        sendHtmlEmail(to, "✅ Verifica tu cuenta — STATELESS", "emails/verificar-email", context);
    }

    public void sendResetPasswordEmail(String to, String url, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("url", url);
        context.setVariable("nombre", name);
        sendHtmlEmail(to, "🔑 Recupera tu contraseña — STATELESS", "emails/recuperar-password", context);
    }
}