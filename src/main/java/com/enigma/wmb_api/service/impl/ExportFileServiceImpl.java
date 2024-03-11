package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.service.ExportFileService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


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
                                csvContent.append(billDetail.getBill().getId()).append(",")
                                        .append(billDetail.getBill().getTransDate()).append(",")
                                        // User
                                        .append(bill.getUser().getUserAccount().getUsername()).append(",")
                                        .append(bill.getUser().getName()).append(",")
                                        .append(bill.getUser().getPhoneNumber()).append(",")
                                        // Table
                                        .append(table).append(",")
                                        // Trans Type
                                        .append(bill.getTransType()).append(",")
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
}
