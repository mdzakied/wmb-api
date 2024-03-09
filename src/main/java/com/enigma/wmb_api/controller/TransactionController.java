package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.APIUrl;
import com.enigma.wmb_api.constant.ResponseMessage;
import com.enigma.wmb_api.dto.request.transaction.PostTransactionRequest;
import com.enigma.wmb_api.dto.request.transaction.SearchTransactionRequest;
import com.enigma.wmb_api.dto.response.common.CommonResponse;
import com.enigma.wmb_api.dto.response.common.CommonResponsePage;
import com.enigma.wmb_api.dto.response.common.PagingResponse;
import com.enigma.wmb_api.dto.response.transaction.TransactionResponse;
import com.enigma.wmb_api.entity.Bill;
import com.enigma.wmb_api.entity.MTable;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.service.TransactionService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.TRANSACTION_API)
public class TransactionController {
    private final TransactionService transactionService;

    // Create Transaction Controller
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<TransactionResponse>> createTransaction(
            @RequestBody PostTransactionRequest postTransactionRequest
    ) {
        // Transaction Response from crate Service
        TransactionResponse transactionResponse = transactionService.create(postTransactionRequest);

        // Common Response
        CommonResponse<TransactionResponse> transactionCommonResponse = CommonResponse.<TransactionResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(ResponseMessage.SUCCESS_SAVE_DATA)
                .data(transactionResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionCommonResponse);
    }

    // Get All Transaction Controller
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponsePage<List<TransactionResponse>>> getAllTransaction(
            @RequestParam(name = "userName", required = false) String userName,
            @RequestParam(name = "menuName", required = false) String menuName,
            @RequestParam(name = "transDate", required = false) @JsonFormat(pattern = "yyyy-MM-dd") String transDate,
            @RequestParam(name = "startTransDate", required = false) @JsonFormat(pattern = "yyyy-MM-dd") String startTransDate,
            @RequestParam(name = "endTransDate", required = false) @JsonFormat(pattern = "yyyy-MM-dd") String endTransDate,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "transDate") String soryBy,
            @RequestParam(name = "direction", defaultValue = "desc") String direction
    ) {
        // Query Params & Pagination to SearchTransactionRequest
        SearchTransactionRequest searchTransactionRequest = SearchTransactionRequest.builder()
                .userName(userName)
                .menuName(menuName)
                .transDate(transDate)
                .startTransDate(startTransDate)
                .endTransDate(endTransDate)
                .page(page)
                .size(size)
                .sortBy(soryBy)
                .direction(direction)
                .build();

        // Page All Transaction Response from getAll Service
        Page<TransactionResponse> transactionResponses = transactionService.getAll(searchTransactionRequest);

        // Paging Response for Common Response
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(transactionResponses.getTotalPages())
                .totalElement(transactionResponses.getTotalElements())
                .page(transactionResponses.getPageable().getPageNumber() + 1)
                .size(transactionResponses.getPageable().getPageSize())
                .hasNext(transactionResponses.hasNext())
                .hasPrevious(transactionResponses.hasPrevious())
                .build();

        // Common Response
        CommonResponsePage<List<TransactionResponse>> response = CommonResponsePage.<List<TransactionResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(transactionResponses.getContent())
                .paging(pagingResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Get Transaction by Id Controller
    @GetMapping(path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<TransactionResponse>> getCustomerById(
            @PathVariable String id
    ) {
        // Transaction Response form getOneById service
        TransactionResponse transactionResponse = transactionService.getOneById(id);

        // Common Response
        CommonResponse<TransactionResponse> response = CommonResponse.<TransactionResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(transactionResponse)
                .build();

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
