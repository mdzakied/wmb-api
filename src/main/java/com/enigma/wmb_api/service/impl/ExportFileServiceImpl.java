package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.service.ExportFileService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class ExportFileServiceImpl implements ExportFileService {
    private static final String CSV_HEADER = "Id Trans,Trans Date,Account Username,User Name,User Phone Number,Table Name,Trans Type,Id Trans Detail,Menu Name,Menu Price, Trans Detail Quantity,Id Payment, Payment Status\n";

    @Override
    public String exportTransactionToCsv(Page<Bill> bills) {
        // String Builder for CSV Content
        StringBuilder csvContent = new StringBuilder();

        // Append Header in CSV
        csvContent.append(CSV_HEADER);

        // Append Data in CSV suitable with header
        bills.forEach(
                bill -> {

                    // Table
                    String table;
                    if (bill.getTable() != null) {
                        table = bill.getTable().getName();
                    } else {
                        table = null;
                    }

                    bill.getBillDetails().forEach(
                            billDetail -> {
                                csvContent
                                        // Transaction
                                        .append(billDetail.getBill().getId()).append(",")
                                        .append(billDetail.getBill().getTransDate()).append(",")
                                        // User
                                        .append(bill.getUser().getUserAccount().getUsername()).append(",")
                                        .append(bill.getUser().getName()).append(",")
                                        .append(bill.getUser().getPhoneNumber()).append(",")
                                        // Table
                                        .append(table).append(",")
                                        // Trans Type
                                        .append(bill.getTransType().getTransTypeEnum().name()).append(",")
                                        // Trans Detail
                                        .append(billDetail.getId()).append(",")
                                        // Menu
                                        .append(billDetail.getMenu().getName()).append(",")
                                        .append(billDetail.getMenu().getPrice()).append(",")
                                        // Additional
                                        .append(billDetail.getMenu().getPrice() * billDetail.getQty()).append(",")
                                        // Payment
                                        .append(bill.getPayment().getId()).append(",")
                                        .append(bill.getPayment().getTransactionStatus()).append(",")
                                        .append("\n");
                            }
                    );
                }
        );

        // Convert to String
        return csvContent.toString();
    }

    @Override
    public ByteArrayInputStream exportTransactionToPdf(Page<Bill> bills) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        document.setPageSize(PageSize.A4.rotate());

        try {

            PdfPTable table = new PdfPTable(14);
            table.setWidthPercentage(80);
            table.setWidths(new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8f);

            PdfPCell hcell;

            hcell = new PdfPCell(new Phrase("No", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Id Trans", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Trans Date", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Account Username", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("User Name", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("User Phone Number", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Table Name", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Trans Type", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Id Trans Detail", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Menu Name", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Menu Price", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Trans Detail Quantity", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Id Payment", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Payment Status", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            // No
            AtomicInteger columnIndex = new AtomicInteger(1);

            bills.forEach(
                    bill -> {

                        // Table
                        String mtable;
                        if (bill.getTable() != null) {
                            mtable = bill.getTable().getName();
                        } else {
                            mtable = "-";
                        }

                        bill.getBillDetails().forEach(
                                billDetail -> {
                                    PdfPCell cell;

                                    // No
                                    cell = new PdfPCell(new Phrase(String.valueOf(columnIndex)));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    // Transaction
                                    cell = new PdfPCell(new Phrase(billDetail.getBill().getId()));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    cell = new PdfPCell(new Phrase(String.valueOf(billDetail.getBill().getTransDate())));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    // User
                                    cell = new PdfPCell(new Phrase(bill.getUser().getUserAccount().getUsername()));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    cell = new PdfPCell(new Phrase(bill.getUser().getName()));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    cell = new PdfPCell(new Phrase(bill.getUser().getPhoneNumber()));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    // Table
                                    cell = new PdfPCell(new Phrase(mtable));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    // Trans Type
                                    cell = new PdfPCell(new Phrase(bill.getTransType().getTransTypeEnum().name()));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    // Trans Detail
                                    cell = new PdfPCell(new Phrase(billDetail.getId()));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    // Menu
                                    cell = new PdfPCell(new Phrase(billDetail.getMenu().getName()));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    cell = new PdfPCell(new Phrase(String.valueOf(billDetail.getMenu().getPrice())));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    // Additional
                                    cell = new PdfPCell(new Phrase(String.valueOf(billDetail.getMenu().getPrice() * billDetail.getQty())));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    // Payment
                                    cell = new PdfPCell(new Phrase(bill.getPayment().getId()));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    cell = new PdfPCell(new Phrase(bill.getPayment().getTransactionStatus()));
                                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    table.addCell(cell);

                                    columnIndex.getAndIncrement();
                                }

                        );
                    }
            );


            PdfWriter.getInstance(document, out);
            document.open();
            document.add(table);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
