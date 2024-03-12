package com.enigma.wmb_api.service;

import com.enigma.wmb_api.entity.Bill;
import com.itextpdf.text.DocumentException;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;


public interface ExportFileService {
    String exportTransactionToCsv(Page<Bill> bills);
    ByteArrayInputStream exportTransactionToPdf(Page<Bill> bills) throws DocumentException;
}
