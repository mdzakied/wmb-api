package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.transaction.PostTransactionRequest;
import com.enigma.wmb_api.dto.request.transaction.SearchTransactionRequest;
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
    private final TransactionDetailRepository transactionDetailRepository;
    private final UserService userService;
    private final TableService tableService;
    private final TransTypeService transTypeService;
    private final MenuService menuService;
    private final ValidationUtil validationUtil;

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
        if (postTransactionRequest.getTableId() != null) {
            table = tableService.getById(postTransactionRequest.getTableId());
            transType = transTypeService.getById("EI");
        } else {
            transType = transTypeService.getById("TA");
        }

        // Create Transaction
        Bill bill = Bill.builder()
                .transDate(new Date())
                .user(user)
                .table(table)
                .transType(transType)
                .build();

        // Create Transaction Detail
        List<BillDetail> billDetails = postTransactionRequest.getTransactionDetails().stream()
                .map(billDetail-> {
                    // Get Menu
                    Menu menu = menuService.getById(billDetail.getMenuId());

                    // Create Transaction Detail
                    BillDetail newBillDetail = BillDetail.builder()
                            .bill(bill)
                            .menu(menu)
                            .qty(billDetail.getQty())
                            .price(billDetail.getQty() * menu.getPrice())
                            .build();

                    transactionDetailRepository.saveAndFlush(newBillDetail);

                    return newBillDetail;
                }).toList();

        // Convert to Transaction Response
        return convertToTransactionResponse(bill, billDetails);
    }

    @Override
    public Page<TransactionResponse> getAll(SearchTransactionRequest searchTransactionRequest) {
        // Validate Page
        if (searchTransactionRequest.getPage() <=0) searchTransactionRequest.setPage(1);
        // Sort
        Sort sort = Sort.by(Sort.Direction.fromString(searchTransactionRequest.getDirection()), searchTransactionRequest.getSortBy());
        // Pageable
        Pageable pageable = PageRequest.of(searchTransactionRequest.getPage() - 1, searchTransactionRequest.getSize(), sort);
        // Specification
        Specification<Bill> specification = TransactionSpecification.getSpecification(searchTransactionRequest);

        // Find All Bill with Pageable
        Page<Bill> billPages = transactionRepository.findAll(specification, pageable);

        // Response
        Page<TransactionResponse> transactionResponsePages = billPages.map(
                transactionResponsePage -> {
                    // Convert to Transaction Response
                    return convertToTransactionResponse(transactionResponsePage, transactionResponsePage.getBillDetails());
                }
        );

        return transactionResponsePages;
    }

    @Override
    public TransactionResponse getOneById(String id) {
            Bill bill = findByIdOrThrowNotFound(id);
            List<BillDetail> billDetails = bill.getBillDetails();

            return convertToTransactionResponse(bill, billDetails);
    }

    @Override
    public Bill getById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    public Bill findByIdOrThrowNotFound (String id) {
        return transactionRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found"));
    }

    public TransactionResponse convertToTransactionResponse(Bill bill, List<BillDetail>  billDetails){

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

        // Response Transaction
        return TransactionResponse.builder()
                .id(bill.getId())
                .transDate(bill.getTransDate())
                .user(userTransactionResponse)
                .table(tableTransactionResponse)
                .transType(transTypeTransactionResponse)
                .transactionDetails(transactionDetailResponses)
                .build();
    }
}
