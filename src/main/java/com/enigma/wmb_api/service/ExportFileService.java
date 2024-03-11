package com.enigma.wmb_api.service;

import com.enigma.wmb_api.entity.Bill;
import org.springframework.data.domain.Page;


public interface ExportFileService {
    String exportTransactionToCsv(Page<Bill> bills);
}
