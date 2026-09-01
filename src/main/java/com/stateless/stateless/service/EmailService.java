package com.stateless.stateless.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.stateless.stateless.model.Envio;
import com.stateless.stateless.model.Venta;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired private JavaMailSender mailSender;
    @Autowired private TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:stateless2000@gmail.com}")
    private String emailRemitente;

    // 1. Verificación de Cuenta
    public void sendVerificationEmail(String to, String url, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("url", url);
        context.setVariable("nombre", name);
        String html = templateEngine.process("emails/verificar-email", context);
        sendHtmlEmail(to, "✅ Verifica tu cuenta - STATELESS", html);
    }

    // 2. Recuperar Contraseña
    public void sendResetPasswordEmail(String to, String url, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("url", url);
        context.setVariable("nombre", name);
        String html = templateEngine.process("emails/recuperar-password", context);
        sendHtmlEmail(to, "🔑 Recupera tu contraseña - STATELESS", html);
    }

    // 3. Confirmación de Pago y Compra Exitosa
    @Async
    public void enviarConfirmacionCompra(Venta venta) {
        if (venta == null || venta.getUsuario() == null || venta.getUsuario().getEmail() == null) {
            return;
        }

        String to = venta.getUsuario().getEmail();
        String subject = "✅ ¡Pago Confirmado! Pedido #" + venta.getId() + " - STATELESS";
        
        String nombre = (venta.getUsuario().getName() != null) ? venta.getUsuario().getName() : "Cliente";
        String total = (venta.getTotal() != null) ? venta.getTotal().toString() : "0";
        String metodo = (venta.getMetodoPago() != null) ? venta.getMetodoPago().toUpperCase() : "TARJETA / ONLINE";

        String html = """
            <div style="background:#000; color:#fff; font-family:'Helvetica Neue', Arial, sans-serif; padding:40px 20px; max-width:600px; margin:0 auto; border:1px solid #222;">
                <div style="text-align:center; border-bottom:1px solid #333; padding-bottom:20px; margin-bottom:30px;">
                    <h1 style="font-size:32px; letter-spacing:4px; margin:0; color:#fff;">STATELESS</h1>
                    <p style="font-size:12px; letter-spacing:2px; text-transform:uppercase; color:#888; margin-top:6px;">Pago Confirmado Exitosamente</p>
                </div>
                
                <p style="font-size:15px; line-height:1.6; color:#ccc;">Hola <strong>%s</strong>,</p>
                <p style="font-size:14px; line-height:1.6; color:#aaa;">Hemos recibido tu pago para la orden <strong>#%d</strong> por un total de <strong>$%s COP</strong>.</p>
                
                <div style="background:#111; border:1px solid #222; padding:20px; margin:24px 0;">
                    <p style="font-size:11px; letter-spacing:2px; text-transform:uppercase; color:#777; margin:0 0 8px;">Método de Pago</p>
                    <p style="font-size:14px; font-weight:bold; color:#fff; margin:0 0 16px;">%s</p>
                    
                    <p style="font-size:11px; letter-spacing:2px; text-transform:uppercase; color:#777; margin:0 0 8px;">Estado</p>
                    <p style="font-size:14px; font-weight:bold; color:#00ff88; margin:0;">PAGO APROBADO</p>
                </div>
                
                <div style="text-align:center; margin:36px 0;">
                    <a href="http://localhost:8081/account/tracking/%d" style="background:#fff; color:#000; padding:14px 28px; font-size:12px; font-weight:bold; letter-spacing:2px; text-transform:uppercase; text-decoration:none; display:inline-block;">
                        Rastrear mi Pedido →
                    </a>
                </div>
                
                <p style="font-size:11px; text-align:center; color:#555; border-top:1px solid #222; padding-top:20px; margin-top:40px;">
                    © STATELESS CLOTHING. Todos los derechos reservados.
                </p>
            </div>
        """.formatted(nombre, venta.getId(), total, metodo, venta.getId());

        try {
            sendHtmlEmail(to, subject, html);
        } catch (MessagingException e) {
            System.err.println("Error al enviar email de confirmación: " + e.getMessage());
        }
    }

    // 4. Notificación de Actualización Logística / Envíos
    @Async
    public void enviarActualizacionLogistica(Venta venta, Envio envio, String nuevoEstado) {
        if (venta == null || venta.getUsuario() == null || venta.getUsuario().getEmail() == null) {
            return;
        }

        String to = venta.getUsuario().getEmail();
        String estadoLegible = switch (nuevoEstado.toLowerCase()) {
            case "en_preparacion", "preparando" -> "EN BODEGA / PREPARACIÓN";
            case "en_curso", "enviado" -> "EN CAMINO A TU DOMICILIO";
            case "entregado" -> "ENTREGADO EN DESTINO";
            default -> nuevoEstado.toUpperCase();
        };

        String subject = "📦 Novedad en tu Envío #" + venta.getId() + ": " + estadoLegible;

        String guiaInfo = (envio != null && envio.getNumeroGuia() != null && !envio.getNumeroGuia().isBlank()) ? 
            "<p style='color:#fff; margin-top:8px;'><strong>Número de Guía:</strong> " + envio.getNumeroGuia() + " (" + (envio.getTransportadora() != null ? envio.getTransportadora() : "Coordinadora") + ")</p>" : "";

        String html = """
            <div style="background:#000; color:#fff; font-family:'Helvetica Neue', Arial, sans-serif; padding:40px 20px; max-width:600px; margin:0 auto; border:1px solid #222;">
                <div style="text-align:center; border-bottom:1px solid #333; padding-bottom:20px; margin-bottom:30px;">
                    <h1 style="font-size:32px; letter-spacing:4px; margin:0; color:#fff;">STATELESS</h1>
                    <p style="font-size:12px; letter-spacing:2px; text-transform:uppercase; color:#00ffff; margin-top:6px;">Actualización de Envío</p>
                </div>
                
                <p style="font-size:15px; color:#ccc;">Tu orden <strong>#%d</strong> tiene una nueva actualización en tiempo real:</p>
                
                <div style="background:#111; border:1px solid #333; padding:24px; text-align:center; margin:24px 0;">
                    <p style="font-size:11px; letter-spacing:2px; text-transform:uppercase; color:#888; margin:0 0 6px;">Estado Actual</p>
                    <h2 style="font-size:20px; letter-spacing:2px; color:#fff; margin:0;">%s</h2>
                    %s
                </div>
                
                <div style="text-align:center; margin:32px 0;">
                    <a href="http://localhost:8081/account/tracking/%d" style="background:#fff; color:#000; padding:14px 28px; font-size:12px; font-weight:bold; letter-spacing:2px; text-transform:uppercase; text-decoration:none; display:inline-block;">
                        Ver Rastreo en Vivo →
                    </a>
                </div>
            </div>
        """.formatted(venta.getId(), estadoLegible, guiaInfo, venta.getId());

        try {
            sendHtmlEmail(to, subject, html);
        } catch (MessagingException e) {
            System.err.println("Error al enviar email de logística: " + e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(emailRemitente);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
}