package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.TransTypeEnum;
import com.enigma.wmb_api.constant.UserRoleEnum;
import com.enigma.wmb_api.dto.request.transaction.PostTransactionRequest;
import com.enigma.wmb_api.dto.request.transaction_detail.PostTransactionDetailRequest;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repositry.TransactionDetailRepository;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.service.TransactionDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TransactionDetailServiceImplTest {
    @Mock
    private TransactionDetailRepository transactionDetailRepository;
    @Mock
    private MenuService menuService;

    private TransactionDetailService transactionDetailService;

    @BeforeEach
    void setUp() {
        transactionDetailService = new TransactionDetailServiceImpl(menuService, transactionDetailRepository);
    }

    @Test
    void shouldReturnBillDetailWhenCreate() {
        // Given Params

        // Given User
        User user = User.builder()
                .id("u-01")
                .name("Dzaki")
                .userAccount(UserAccount.builder()
                        .id("ua-01")
                        .username("Dzaki")
                        .password("password")
                        .role(List.of(
                                Role.builder()
                                        .id("r-01")
                                        .role(UserRoleEnum.ROLE_CUSTOMER)
                                        .build()
                        ))
                        .build())
                .build();

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("T01")
                .build();

        // Given Trans Type
        TransType transType = TransType.builder()
                .transTypeEnum(TransTypeEnum.EI)
                .build();

        // Given Payment
        Payment payment = Payment.builder()
                .id("py-01")
                .redirectUrl("/to/Payment.html")
                .token("thisToken")
                .build();

        // Given Transaction
        Bill transaction = Bill.builder()
                .id("trans-01")
                .transDate(new Date())
                .user(user)
                .table(table)
                .transType(transType)
                .payment(payment)
                .build();


        // Given Params
        PostTransactionDetailRequest parameterTransactionDetails = PostTransactionDetailRequest.builder()
                .menuId("m-01")
                .qty(5)
                .build();

        // Given Menu
        Menu menu = Menu.builder()
                .id("m-01")
                .name("Nasi Putih")
                .price(5000)
                .build();

        // Stubbing Config find menu transaction
        Mockito.when(menuService.getById(Mockito.any()))
                .thenReturn(menu);

        // Given Transaction Detail
        BillDetail billDetail = BillDetail.builder()
                .bill(transaction)
                .menu(Menu.builder()
                        .id(parameterTransactionDetails.getMenuId())
                        .build()
                )
                .qty(parameterTransactionDetails.getQty())
                .build();

        // Stubbing Config save transaction detail
        Mockito.when(transactionDetailRepository.saveAndFlush(Mockito.any()))
                .thenReturn(billDetail);

        // When
        BillDetail acctualBillDetail = transactionDetailService.create(transaction, parameterTransactionDetails);

        // Then
        assertEquals(transaction, acctualBillDetail.getBill());
    }
}