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
        try {
            Context context = new Context();
            context.setVariable("venta", venta);

            // Renderizar el HTML
            String htmlContent = templateEngine.process("checkout/factura-pdf", context);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                
                // Esta línea evita que el PDF falle por culpa de imágenes con rutas relativas
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(outputStream);
                
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            // Imprimir error exacto en la consola de Docker
            System.err.println("ERROR GENERANDO PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }
}