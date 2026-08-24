package com.stateless.stateless.service;

import com.stateless.stateless.model.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
public class FacturaPdfService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generarFacturaPdf(Venta venta) {
        // 1. Preparar el contexto de Thymeleaf (Equivalente a compact('venta'))
        Context context = new Context();
        context.setVariable("venta", venta);

        // 2. Renderizar el HTML a un String
        String htmlContent = templateEngine.process("checkout/factura-pdf", context);

        // 3. Convertir HTML a PDF usando Flying Saucer
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de la factura", e);
        }
    }
}