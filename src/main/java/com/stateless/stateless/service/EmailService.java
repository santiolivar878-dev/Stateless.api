package com.stateless.stateless.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

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

    // 3. Confirmación de Pago y Compra Exitosa (Síncrono para ver el log en vivo)
    public void enviarConfirmacionCompra(Venta venta) {
        try {
            System.out.println("🚀 [EMAIL SERVICE] Iniciando envío de confirmación de compra...");
            
            String to = (venta != null && venta.getUsuario() != null && venta.getUsuario().getEmail() != null)
                    ? venta.getUsuario().getEmail()
                    : emailRemitente;

            if (to.endsWith("@example.com") || to.contains("admin@")) {
                to = emailRemitente;
                System.out.println("ℹ️ [EMAIL] Redirigiendo a tu correo real para prueba: " + to);
            }

            Long ventaId = (venta != null) ? venta.getId() : 0L;
            String nombre = (venta != null && venta.getUsuario() != null && venta.getUsuario().getName() != null) ? venta.getUsuario().getName() : "Cliente";
            String total = (venta != null && venta.getTotal() != null) ? venta.getTotal().toString() : "0";
            String metodo = (venta != null && venta.getMetodoPago() != null) ? venta.getMetodoPago().toUpperCase() : "TARJETA / ONLINE";
            String trackingUrl = baseUrl + "/account/tracking/" + ventaId;

            String subject = "✅ ¡Pago Confirmado! Pedido #" + ventaId + " - STATELESS";

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
                        <a href="%s" style="background:#fff; color:#000; padding:14px 28px; font-size:12px; font-weight:bold; letter-spacing:2px; text-transform:uppercase; text-decoration:none; display:inline-block;">
                            Rastrear mi Pedido →
                        </a>
                    </div>
                    
                    <p style="font-size:11px; text-align:center; color:#555; border-top:1px solid #222; padding-top:20px; margin-top:40px;">
                        © STATELESS CLOTHING. Todos los derechos reservados.
                    </p>
                </div>
            """.formatted(nombre, ventaId, total, metodo, trackingUrl);

            System.out.println("📨 [EMAIL] Conectando con smtp.gmail.com para entregar a: " + to);
            sendHtmlEmail(to, subject, html);
            System.out.println("✅ [EMAIL] ¡Correo entregado con ÉXITO a Gmail para: " + to + "!");
        } catch (Exception e) {
            System.err.println("❌ [EMAIL ERROR CRÍTICO]: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 4. Notificación Logística - Estética Exclusiva STATELESS
    public void enviarActualizacionLogistica(String to, Long ordenId, String transportadora, String numeroGuia, String nuevoEstado) {
        try {
            String destinatarioReal = (to != null && !to.isBlank() && !to.endsWith("@example.com")) ? to : emailRemitente;

            String estadoLegible = switch (nuevoEstado.toLowerCase().trim()) {
                case "en_preparacion", "preparando" -> "EN BODEGA / PREPARACIÓN";
                case "en_curso", "enviado" -> "EN CAMINO A TU DOMICILIO";
                case "entregado" -> "ENTREGADO EN DESTINO";
                default -> nuevoEstado.toUpperCase();
            };

            String subject = "📦 Estado de tu Orden #" + ordenId + " - " + estadoLegible + " | STATELESS";
            String transportadoraTxt = (transportadora != null && !transportadora.isBlank()) ? transportadora : "Coordinadora / Servientrega";
            String guiaTxt = (numeroGuia != null && !numeroGuia.isBlank()) ? numeroGuia : "PENDIENTE DE ASIGNACIÓN";
            String trackingUrl = baseUrl + "/account/tracking/" + ordenId;

            String html = """
                <div style="background:#000000; color:#ffffff; font-family:'Helvetica Neue', Helvetica, Arial, sans-serif; padding:48px 24px; max-width:580px; margin:0 auto; border:1px solid #1a1a1a;">
                    <!-- HEADER -->
                    <div style="text-align:center; border-bottom:1px solid #222222; padding-bottom:28px; margin-bottom:32px;">
                        <h1 style="font-size:30px; font-weight:900; letter-spacing:6px; margin:0; color:#ffffff;">STATELESS</h1>
                        <p style="font-size:10px; font-weight:700; letter-spacing:3px; text-transform:uppercase; color:#888888; margin-top:8px;">Logística & Despacho Nacional</p>
                    </div>
                    
                    <!-- MENSAJE PRINCIPAL -->
                    <p style="font-size:14px; line-height:1.6; color:#cccccc; margin-bottom:12px;">Estimado cliente,</p>
                    <p style="font-size:13px; line-height:1.7; color:#999999; margin-bottom:28px;">
                        Tu pedido <strong>#%d</strong> presenta un nuevo movimiento en nuestra red de distribución.
                    </p>
                    
                    <!-- STATUS CARD STATELESS -->
                    <div style="background:#0a0a0a; border:1px solid #222222; padding:28px 20px; text-align:center; margin:28px 0;">
                        <span style="font-size:10px; font-weight:700; letter-spacing:2.5px; text-transform:uppercase; color:#666666; display:block; margin-bottom:8px;">Estado del Envío</span>
                        <div style="display:inline-block; border:1px solid #ffffff; padding:8px 18px; margin-bottom:24px;">
                            <span style="font-size:13px; font-weight:800; letter-spacing:2px; color:#ffffff; text-transform:uppercase;">%s</span>
                        </div>
                        
                        <!-- TABLA DE DETALLES -->
                        <table style="width:100%%; border-collapse:collapse; margin-top:10px; text-align:left; border-top:1px solid #1a1a1a;">
                            <tr>
                                <td style="padding:12px 6px; font-size:11px; letter-spacing:1px; text-transform:uppercase; color:#666666;">Transportadora</td>
                                <td style="padding:12px 6px; font-size:12px; font-weight:600; text-align:right; color:#ffffff;">%s</td>
                            </tr>
                            <tr style="border-top:1px solid #141414;">
                                <td style="padding:12px 6px; font-size:11px; letter-spacing:1px; text-transform:uppercase; color:#666666;">Número de Guía</td>
                                <td style="padding:12px 6px; font-size:13px; font-weight:700; font-family:monospace; text-align:right; color:#ffffff; letter-spacing:1px;">%s</td>
                            </tr>
                        </table>
                    </div>
                    
                    <!-- BOTÓN CTA MONOCROMÁTICO -->
                    <div style="text-align:center; margin:36px 0;">
                        <a href="%s" style="background:#ffffff; color:#000000; padding:15px 32px; font-size:11px; font-weight:800; letter-spacing:3px; text-transform:uppercase; text-decoration:none; display:inline-block;">
                            Rastrear Pedido en Vivo →
                        </a>
                    </div>
                    
                    <!-- FOOTER -->
                    <div style="border-top:1px solid #1a1a1a; padding-top:28px; margin-top:40px; text-align:center;">
                        <p style="font-size:10px; letter-spacing:1px; color:#444444; margin:0 0 6px;">Este es un mensaje generado automáticamente por el sistema logístico de STATELESS.</p>
                        <p style="font-size:10px; letter-spacing:2px; text-transform:uppercase; color:#333333; margin:0;">
                            © STATELESS CLOTHING. All rights reserved.
                        </p>
                    </div>
                </div>
            """.formatted(ordenId, estadoLegible, transportadoraTxt, guiaTxt, trackingUrl);

            System.out.println("-> [EMAIL LOGÍSTICA] Despachando notificación hacia: " + destinatarioReal);
            sendHtmlEmail(destinatarioReal, subject, html);
            System.out.println("✅ [EMAIL LOGÍSTICA] Notificación entregada con éxito a: " + destinatarioReal);
        } catch (Exception e) {
            System.err.println("❌ [EMAIL LOGÍSTICA ERROR]: " + e.getMessage());
        }
    }

    public void enviarActualizacionLogistica(Venta venta, Envio envio, String nuevoEstado) {
        String to = (venta != null && venta.getUsuario() != null) ? venta.getUsuario().getEmail() : emailRemitente;
        Long ordenId = (venta != null) ? venta.getId() : 0L;
        String transportadora = (envio != null) ? envio.getTransportadora() : null;
        String guia = (envio != null) ? envio.getNumeroGuia() : null;
        enviarActualizacionLogistica(to, ordenId, transportadora, guia, nuevoEstado);
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