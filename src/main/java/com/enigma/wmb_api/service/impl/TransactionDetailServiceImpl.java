package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.transaction_detail.PostTransactionDetailRequest;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.entity.BillDetail;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.repositry.TransactionDetailRepository;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.service.TransactionDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionDetailServiceImpl implements TransactionDetailService {
    private final MenuService menuService;
    private final TransactionDetailRepository transactionDetailRepository;
    @Override
    public BillDetail create(Bill bill, PostTransactionDetailRequest postTransactionDetailRequest) {
        // Get Menu from getById service
        Menu menu = menuService.getById(postTransactionDetailRequest.getMenuId());

        // Create Transaction Detail
        BillDetail billDetail = BillDetail.builder()
                .bill(bill)
                .menu(menu)
                .qty(postTransactionDetailRequest.getQty())
                .price(postTransactionDetailRequest.getQty() * menu.getPrice())
                .build();

        // Save to Repository
        transactionDetailRepository.saveAndFlush(billDetail);

        return billDetail;
    }
}
