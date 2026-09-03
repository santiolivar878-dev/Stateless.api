package com.stateless.stateless.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.model.VentaItem;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Service
public class FacturaPdfService {

    public byte[] generarFacturaPdf(Venta venta) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            DecimalFormat df = new DecimalFormat("#,##0");

            // FUENTES
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLACK);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(100, 100, 100));
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
            Font boldText = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

            // ENCABEZADO: LOGO Y DATOS FISCALES
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{60, 40});

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.addElement(new Paragraph("STATELESS", titleFont));
            logoCell.addElement(new Paragraph("STATELESS CLOTHING S.A.S. | NIT: 901.458.789-1", subtitleFont));
            logoCell.addElement(new Paragraph("Régimen Común • Factura Electrónica de Venta", textFont));
            logoCell.addElement(new Paragraph("Bogotá D.C., Colombia", textFont));
            headerTable.addCell(logoCell);

            PdfPCell invoiceCell = new PdfPCell();
            invoiceCell.setBorder(Rectangle.NO_BORDER);
            invoiceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph invNum = new Paragraph("FACTURA N° ST-" + String.format("%06d", venta.getId()), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK));
            invNum.setAlignment(Element.ALIGN_RIGHT);
            invoiceCell.addElement(invNum);

            String fechaStr = (venta.getCreatedAt() != null) 
                    ? venta.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) 
                    : "Fecha actual";
            Paragraph fechaP = new Paragraph("Fecha de Emisión: " + fechaStr, textFont);
            fechaP.setAlignment(Element.ALIGN_RIGHT);
            invoiceCell.addElement(fechaP);

            Paragraph estadoP = new Paragraph("Estado: PAGO CONFIRMADO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(0, 150, 80)));
            estadoP.setAlignment(Element.ALIGN_RIGHT);
            invoiceCell.addElement(estadoP);

            headerTable.addCell(invoiceCell);
            document.add(headerTable);

            document.add(new Paragraph(" "));

            // DATOS DEL CLIENTE Y ENVÍO
            PdfPTable clientTable = new PdfPTable(2);
            clientTable.setWidthPercentage(100);
            clientTable.setWidths(new float[]{50, 50});

            PdfPCell c1 = new PdfPCell();
            c1.setBackgroundColor(new Color(248, 248, 248));
            c1.setPadding(10);
            c1.setBorderColor(new Color(225, 225, 225));
            c1.addElement(new Paragraph("ADQUIRIENTE / CLIENTE", subtitleFont));
            String nombreCli = (venta.getUsuario() != null && venta.getUsuario().getName() != null) ? venta.getUsuario().getName() : "Cliente";
            String emailCli = (venta.getUsuario() != null && venta.getUsuario().getEmail() != null) ? venta.getUsuario().getEmail() : "N/A";
            c1.addElement(new Paragraph("Nombre: " + nombreCli, boldText));
            c1.addElement(new Paragraph("Email: " + emailCli, textFont));
            clientTable.addCell(c1);

            PdfPCell c2 = new PdfPCell();
            c2.setBackgroundColor(new Color(248, 248, 248));
            c2.setPadding(10);
            c2.setBorderColor(new Color(225, 225, 225));
            c2.addElement(new Paragraph("DATOS DE ENTREGA & PAGO", subtitleFont));
            String dir = (venta.getEnvio() != null && venta.getEnvio().getDireccion() != null) ? venta.getEnvio().getDireccion() : "Entrega a domicilio";
            String ciudad = (venta.getEnvio() != null && venta.getEnvio().getCiudad() != null) ? venta.getEnvio().getCiudad() : "Colombia";
            String metodo = (venta.getMetodoPago() != null) ? venta.getMetodoPago().toUpperCase() : "TARJETA / PASARELA";
            c2.addElement(new Paragraph("Destino: " + dir + ", " + ciudad, textFont));
            c2.addElement(new Paragraph("Método de Pago: " + metodo, textFont));
            clientTable.addCell(c2);

            document.add(clientTable);
            document.add(new Paragraph(" "));

            // TABLA DE ITEMS
            PdfPTable itemsTable = new PdfPTable(5);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{40, 15, 15, 15, 15});

            String[] headers = {"Descripción de Prenda", "Cant.", "Precio Unit.", "IVA (19%)", "Subtotal"};
            for (String h : headers) {
                PdfPCell th = new PdfPCell(new Phrase(h, headerFont));
                th.setBackgroundColor(Color.BLACK);
                th.setPadding(6);
                th.setHorizontalAlignment(Element.ALIGN_CENTER);
                itemsTable.addCell(th);
            }

            if (venta.getItems() != null) {
                for (VentaItem item : venta.getItems()) {
                    String desc = (item.getProducto() != null) ? item.getProducto().getNombre() : "Prenda Stateless";
                    if (item.getVariante() != null && item.getVariante().getColor() != null) {
                        desc += " (" + item.getVariante().getColor() + ")";
                    }
                    PdfPCell tdDesc = new PdfPCell(new Phrase(desc, textFont));
                    tdDesc.setPadding(6);
                    itemsTable.addCell(tdDesc);

                    PdfPCell tdCant = new PdfPCell(new Phrase(String.valueOf(item.getCantidad()), textFont));
                    tdCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tdCant.setPadding(6);
                    itemsTable.addCell(tdCant);

                    BigDecimal unit = item.getPrecioUnitario() != null ? item.getPrecioUnitario() : BigDecimal.ZERO;
                    PdfPCell tdUnit = new PdfPCell(new Phrase("$ " + df.format(unit), textFont));
                    tdUnit.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tdUnit.setPadding(6);
                    itemsTable.addCell(tdUnit);

                    BigDecimal subItem = unit.multiply(new BigDecimal(item.getCantidad()));
                    BigDecimal ivaItem = subItem.subtract(subItem.divide(new BigDecimal("1.19"), 0, RoundingMode.HALF_UP));
                    PdfPCell tdIva = new PdfPCell(new Phrase("$ " + df.format(ivaItem), textFont));
                    tdIva.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tdIva.setPadding(6);
                    itemsTable.addCell(tdIva);

                    PdfPCell tdSub = new PdfPCell(new Phrase("$ " + df.format(subItem), boldText));
                    tdSub.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tdSub.setPadding(6);
                    itemsTable.addCell(tdSub);
                }
            }
            document.add(itemsTable);

            // LIQUIDACIÓN TRIBUTARIA (BASE, IVA 19%, TOTAL)
            BigDecimal total = venta.getTotal() != null ? venta.getTotal() : BigDecimal.ZERO;
            BigDecimal baseNeta = total.divide(new BigDecimal("1.19"), 0, RoundingMode.HALF_UP);
            BigDecimal totalIva = total.subtract(baseNeta);

            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(50);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setWidths(new float[]{60, 40});

            addTotalRow(totalTable, "Subtotal Base Imponible:", "$ " + df.format(baseNeta), textFont);
            addTotalRow(totalTable, "IVA Discriminado (19%):", "$ " + df.format(totalIva), textFont);
            addTotalRow(totalTable, "Despacho Express Nacional:", "GRATIS ($ 0)", textFont);

            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL FACTURA COP:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
            totalLabel.setBackgroundColor(Color.BLACK);
            totalLabel.setPadding(8);
            totalTable.addCell(totalLabel);

            PdfPCell totalVal = new PdfPCell(new Phrase("$ " + df.format(total), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE)));
            totalVal.setBackgroundColor(Color.BLACK);
            totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalVal.setPadding(8);
            totalTable.addCell(totalVal);

            document.add(new Paragraph(" "));
            document.add(totalTable);

            // FOOTER LEGAL
            Paragraph footer = new Paragraph("\nEsta factura es un título valor según el Artículo 774 del Código de Comercio colombiano. Para soporte o devoluciones escribe a stateless2000@gmail.com", FontFactory.getFont(FontFactory.HELVETICA, 7, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private void addTotalRow(PdfPTable table, String label, String val, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, font));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(4);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(val, font));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setPadding(4);
        table.addCell(c2);
    }
}