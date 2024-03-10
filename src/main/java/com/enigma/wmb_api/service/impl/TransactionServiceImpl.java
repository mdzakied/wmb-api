package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.constant.TransTypeEnum;
import com.enigma.wmb_api.dto.request.transaction.PostTransactionRequest;
import com.enigma.wmb_api.dto.request.transaction.SearchTransactionRequest;
import com.enigma.wmb_api.dto.response.PaymentResponse;
import com.enigma.wmb_api.dto.response.transaction.TableTransactionResponse;
import com.enigma.wmb_api.dto.response.transaction.TransTypeTransactionResponse;
import com.enigma.wmb_api.dto.response.transaction.TransactionResponse;
import com.enigma.wmb_api.dto.response.transaction.UserTransactionResponse;
import com.enigma.wmb_api.dto.response.transaction_detail.MenuTransactionDetailResponse;
import com.enigma.wmb_api.dto.response.transaction_detail.TransactionDetailResponse;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.repositry.*;
import com.enigma.wmb_api.service.*;
import com.enigma.wmb_api.specification.TransactionSpecification;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionDetailService transactionDetailService;
    private final UserService userService;
    private final TableService tableService;
    private final TransTypeService transTypeService;
    private final PaymentService paymentService;
    private final ValidationUtil validationUtil;

    // Create Transaction Service
    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransactionResponse create(PostTransactionRequest postTransactionRequest) {
        // Validate postTransactionRequest
        validationUtil.validate(postTransactionRequest);
        // Validate postTransactionRequest.getTransactionDetails
        postTransactionRequest.getTransactionDetails().forEach(validationUtil::validate);

        // Get User
        User user = userService.getById(postTransactionRequest.getUserId());
        // Get Table & Trans Type
        MTable table = null;
        TransType transType;
        // Table not null -> Eat In
        // Table null -> Take Away
        if (postTransactionRequest.getTableId() != null) {
            table = tableService.getById(postTransactionRequest.getTableId());
            transType = transTypeService.getById(TransTypeEnum.EI.name());
        } else {
            transType = transTypeService.getById(TransTypeEnum.TA.name());
        }

        // Create Transaction
        Bill bill = Bill.builder()
                .transDate(new Date())
                .user(user)
                .table(table)
                .transType(transType)
                .build();

        // Create Transaction Detail from Request
        List<BillDetail> billDetails = postTransactionRequest.getTransactionDetails().stream()
                .map( postTransactionDetailRequest -> {
                    // Create Transaction Detail from create service
                    return transactionDetailService.create(bill, postTransactionDetailRequest);
                })
                .toList();

        // Set Bill Details
        bill.setBillDetails(billDetails);

        // Payment from create service
        Payment payment = paymentService.createPayment(bill);

        // Set Payment
        bill.setPayment(payment);

        // Convert to Transaction Response
        return convertToTransactionResponse(bill, billDetails);
    }

    // Get All Transaction Service
    @Transactional(readOnly = true)
    @Override
    public Page<TransactionResponse> getAll(SearchTransactionRequest searchTransactionRequest) {
        // Validate Page
        if (searchTransactionRequest.getPage() <= 0) searchTransactionRequest.setPage(1);
        // Sort
        Sort sort = Sort.by(Sort.Direction.fromString(searchTransactionRequest.getDirection()), searchTransactionRequest.getSortBy());
        // Pageable
        Pageable pageable = PageRequest.of(searchTransactionRequest.getPage() - 1, searchTransactionRequest.getSize(), sort);
        // Specification
        Specification<Bill> specification = TransactionSpecification.getSpecification(searchTransactionRequest);

        // Find All Bill with Pageable
        Page<Bill> billPages = transactionRepository.findAll(specification, pageable);

        // Response Page
        return billPages.map(
                transactionResponsePage -> {
                    // Convert to Transaction Response
                    return convertToTransactionResponse(transactionResponsePage, transactionResponsePage.getBillDetails());
                }
        );
    }

    // Get Transaction By Id Service
    @Transactional(readOnly = true)
    @Override
    public TransactionResponse getOneById(String id) {
        // Find by Id
        Bill bill = findByIdOrThrowNotFound(id);
        List<BillDetail> billDetails = bill.getBillDetails();

        // Convert to Transaction Response
        return convertToTransactionResponse(bill, billDetails);
    }

    // Find Transaction or Throw Error Service
    @Transactional(readOnly = true)
    public Bill findByIdOrThrowNotFound(String id) {
        // Find By id throw error
        return transactionRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                ResponseMessage.ERROR_NOT_FOUND)
        );
    }

    // Convert Transaction to Response
    public TransactionResponse convertToTransactionResponse(Bill bill, List<BillDetail> billDetails) {

        // Response Transaction Detail
        List<TransactionDetailResponse> transactionDetailResponses = billDetails.stream()
                .map(billDetail -> {
                    // Response Menu Transaction Detail
                    MenuTransactionDetailResponse menuTransactionDetailResponse = MenuTransactionDetailResponse.builder()
                            .id(billDetail.getMenu().getId())
                            .name(billDetail.getMenu().getName())
                            .price(billDetail.getMenu().getPrice())
                            .build();

                    // Response Transaction Detail
                    return TransactionDetailResponse.builder()
                            .id(billDetail.getId())
                            .menu(menuTransactionDetailResponse)
                            .qty(billDetail.getQty())
                            .price(billDetail.getPrice())
                            .build();
                }).toList();

        // Response User Transaction
        UserTransactionResponse userTransactionResponse = UserTransactionResponse.builder()
                .id(bill.getUser().getId())
                .name(bill.getUser().getName())
                .phoneNumber(bill.getUser().getPhoneNumber())
                .build();

        // Response Table Transaction
        TableTransactionResponse tableTransactionResponse = null;
        if (bill.getTable() != null) {
            tableTransactionResponse = TableTransactionResponse.builder()
                    .id(bill.getTable().getId())
                    .name(bill.getTable().getName())
                    .build();
        }

        // Response Trans Type Transaction
        TransTypeTransactionResponse transTypeTransactionResponse = TransTypeTransactionResponse.builder()
                .id(bill.getTransType().getTransTypeEnum().name())
                .desc(bill.getTransType().getDescription())
                .build();

        // Response Payment
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(bill.getPayment().getId())
                .token(bill.getPayment().getToken())
                .redirectUrl(bill.getPayment().getRedirectUrl())
                .transactionStatus(bill.getPayment().getTransactionStatus())
                .build();;

        // Response Transaction
        return TransactionResponse.builder()
                .id(bill.getId())
                .transDate(bill.getTransDate())
                .user(userTransactionResponse)
                .table(tableTransactionResponse)
                .transType(transTypeTransactionResponse)
                .transactionDetails(transactionDetailResponses)
                .payment(paymentResponse)
                .build();
    }
}
