package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.TransTypeEnum;
import com.enigma.wmb_api.constant.UserRoleEnum;
import com.enigma.wmb_api.dto.request.transaction.PostTransactionRequest;
import com.enigma.wmb_api.dto.request.transaction.SearchTransactionRequest;
import com.enigma.wmb_api.dto.request.transaction_detail.PostTransactionDetailRequest;
import com.enigma.wmb_api.dto.response.transaction.TransactionResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repositry.TransactionRepository;
import com.enigma.wmb_api.service.*;
import com.enigma.wmb_api.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionDetailService transactionDetailService;

    @Mock
    private UserService userService;

    @Mock
    private TableService tableService;

    @Mock
    private TransTypeService transTypeService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ValidationUtil validationUtil;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionServiceImpl(transactionRepository, transactionDetailService, userService, tableService, transTypeService, paymentService, validationUtil);
    }

    @Test
    void shouldReturnTransactionResponseWhenCreate() {
        // Given Params
        PostTransactionRequest parameterTransaction = PostTransactionRequest.builder()
                .userId("u-01")
                .tableId("t-01")
                .transactionDetails(List.of(
                        PostTransactionDetailRequest.builder()
                                .menuId("m-01")
                                .qty(5)
                                .build()
                ))
                .build();

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

        // Stubbing Config find user transaction
        Mockito.when(userService.getById(user.getId()))
                .thenReturn(user);

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("T01")
                .build();

        // Stubbing Config find table transaction
        Mockito.when(tableService.getById(table.getId()))
                .thenReturn(table);

        // Given Trans Type
        TransType transType = TransType.builder()
                .transTypeEnum(TransTypeEnum.EI)
                .build();

        // Stubbing Config find transType transaction
        Mockito.when(transTypeService.getById(transType.getTransTypeEnum().name()))
                .thenReturn(transType);

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

        // Stubbing config create payment transaction
        Mockito.when(paymentService.createPayment(Mockito.any(Bill.class)))
                .thenReturn(payment);

        // Given Transaction Detail
        List<BillDetail> billDetails = new ArrayList<>();

        for (PostTransactionDetailRequest postTransactionDetailRequest : parameterTransaction.getTransactionDetails()) {
            int increment = 0;
            BillDetail billDetail = BillDetail.builder()
                    .id("tans-dt-" + ++increment)
                    .bill(transaction)
                    .menu(Menu.builder()
                            .id(postTransactionDetailRequest.getMenuId())
                            .build())
                    .qty(postTransactionDetailRequest.getQty())
                    .build();

            billDetails.add(billDetail);

            // Stubbing Config save transaction detail
            Mockito.when(transactionDetailService.create(Mockito.any(Bill.class), Mockito.any(PostTransactionDetailRequest.class)))
                    .thenReturn(billDetail);
        }

        // When
        TransactionResponse actualTransaction = transactionService.create(parameterTransaction);

        // Then
        assertEquals(parameterTransaction.getUserId(), actualTransaction.getUser().getId());
        assertEquals(parameterTransaction.getTransactionDetails().size(), actualTransaction.getTransactionDetails().size());
    }

    @Test
    void shouldReturnPageableTransactionResponseWhenGetAll() {
        // Given User
        User user = User.builder()
                .id("u-01")
                .name("Dzaki")
                .userAccount(
                        UserAccount.builder()
                                .id("ua-01")
                                .username("Dzaki")
                                .password("password")
                                .role(List.of(
                                        Role.builder()
                                                .id("r-01")
                                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                                .build()
                                ))
                                .build()
                )
                .build();

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("T01")
                .build();

        // Given Transaction
        Bill transaction = Bill.builder()
                .id("trans-01")
                .transDate(new Date())
                .user(user)
                .table(table)
                .transType(TransType.builder()
                        .transTypeEnum(TransTypeEnum.EI)
                        .build())
                .billDetails(List.of(
                        BillDetail.builder()
                                .menu(Menu.builder()
                                        .id("m-01")
                                        .name("Nasi Putih")
                                        .price(5000)
                                        .build())
                                .build()
                ))
                .payment(Payment.builder()
                        .id("py-01")
                        .redirectUrl("/to/Payment.html")
                        .token("thisToken")
                        .build())
                .build();

        // Given List Menu
        List<Bill> transactions = List.of(
                transaction
        );

        // Given Pageable from Transactions
        Pageable pageable = PageRequest.of(1, 1);
        Page<Bill> transactionPages = new PageImpl<>(transactions, pageable, transactions.size());

        // Stubbing Config find all transaction
        Mockito.when(transactionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(transactionPages);

        // When
        Page<TransactionResponse> actualTransaction = transactionService.getAll(SearchTransactionRequest.builder()
                .page(1)
                .size(10)
                .sortBy("transDate")
                .direction("desc")
                .build());

        // Then
        assertEquals(transactionPages.getTotalElements(), actualTransaction.getTotalElements());
    }

    @Test
    void shouldReturnTransactionResponseWhenGetOneById() {
        // Given Param
        String id = "trans-01";

        // Given User
        User user = User.builder()
                .id("u-01")
                .name("Dzaki")
                .userAccount(
                        UserAccount.builder()
                                .id("ua-01")
                                .username("Dzaki")
                                .password("password")
                                .role(List.of(
                                        Role.builder()
                                                .id("r-01")
                                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                                .build()
                                ))
                                .build()
                )
                .build();

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("T01")
                .build();

        // Given Transaction
        Bill transaction = Bill.builder()
                .id("trans-01")
                .transDate(new Date())
                .user(user)
                .table(table)
                .transType(TransType.builder()
                        .transTypeEnum(TransTypeEnum.EI)
                        .build())
                .billDetails(List.of(
                        BillDetail.builder()
                                .menu(Menu.builder()
                                        .id("m-01")
                                        .name("Nasi Putih")
                                        .price(5000)
                                        .build())
                                .build()
                ))
                .payment(Payment.builder()
                        .id("py-01")
                        .redirectUrl("/to/Payment.html")
                        .token("thisToken")
                        .build())
                .build();

        // Stubbing Config find menu
        Mockito.when(transactionRepository.findById(id))
                .thenReturn(Optional.of(transaction));

        // When
        TransactionResponse actualTransaction = transactionService.getOneById(id);

        // Then
        assertEquals(id, actualTransaction.getId());
    }

    @Test
    void shouldReturnPageableTransactionWhenGetAll() {
        // Given User
        User user = User.builder()
                .id("u-01")
                .name("Dzaki")
                .userAccount(
                        UserAccount.builder()
                                .id("ua-01")
                                .username("Dzaki")
                                .password("password")
                                .role(List.of(
                                        Role.builder()
                                                .id("r-01")
                                                .role(UserRoleEnum.ROLE_CUSTOMER)
                                                .build()
                                ))
                                .build()
                )
                .build();

        // Given Table
        MTable table = MTable.builder()
                .id("t-01")
                .name("T01")
                .build();

        // Given Transaction
        Bill transaction = Bill.builder()
                .id("trans-01")
                .transDate(new Date())
                .user(user)
                .table(table)
                .transType(TransType.builder()
                        .transTypeEnum(TransTypeEnum.EI)
                        .build())
                .billDetails(List.of(
                        BillDetail.builder()
                                .menu(Menu.builder()
                                        .id("m-01")
                                        .name("Nasi Putih")
                                        .price(5000)
                                        .build())
                                .build()
                ))
                .payment(Payment.builder()
                        .id("py-01")
                        .redirectUrl("/to/Payment.html")
                        .token("thisToken")
                        .build())
                .build();

        // Given List Menu
        List<Bill> transactions = List.of(
                transaction
        );

        // Given Pageable from Transactions
        Pageable pageable = PageRequest.of(1, 1);
        Page<Bill> transactionPages = new PageImpl<>(transactions, pageable, transactions.size());

        // Stubbing Config find all transaction
        Mockito.when(transactionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(transactionPages);

        // When
        Page<Bill> actualTransaction = transactionService.getDataAll(SearchTransactionRequest.builder()
                .page(1)
                .size(10)
                .sortBy("transDate")
                .direction("desc")
                .build());

        // Then
        assertEquals(transactionPages.getTotalElements(), actualTransaction.getTotalElements());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenGetById() {
        assertThrows(RuntimeException.class, () -> {
            transactionService.getOneById("Random Id");
        });
    }
}