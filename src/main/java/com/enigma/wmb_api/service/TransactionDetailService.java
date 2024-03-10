package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.transaction_detail.PostTransactionDetailRequest;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.entity.BillDetail;

public interface TransactionDetailService {
    BillDetail create(Bill bill, PostTransactionDetailRequest postTransactionDetailRequest);
}
