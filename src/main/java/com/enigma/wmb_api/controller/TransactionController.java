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
import com.enigma.wmb_api.repositry.TransactionRepository;
import com.enigma.wmb_api.service.ExportFileService;
import com.enigma.wmb_api.service.TransactionService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.TRANSACTION_API)
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    private final ExportFileService exportFileService;

    // Create Transaction Controller
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
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

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
    @GetMapping("/csv")
    public ResponseEntity<byte[]> generateCsvFile(
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
        Page<Bill> transactions = transactionService.getDataAll(searchTransactionRequest);

        // Create Headers
        HttpHeaders headers = new HttpHeaders();
        // Set Content Headers
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "transaction.csv");

        // Export data transaction to csvBytes
        byte[] csvBytes = exportFileService.exportTransactionToCsv(transactions).getBytes();

        // Set Header Value
        String headerValue = String.format("attachment; filename=%s", headers.getContentDisposition().getFilename());

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(csvBytes);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Private : Have Role Authorization", description = "Role : Superadmin and Admin")
    @GetMapping(
            value = "/pdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<InputStreamResource> generatePdfFile(
            @RequestParam(name = "userName", required = false) String userName,
            @RequestParam(name = "menuName", required = false) String menuName,
            @RequestParam(name = "transDate", required = false) @JsonFormat(pattern = "yyyy-MM-dd") String transDate,
            @RequestParam(name = "startTransDate", required = false) @JsonFormat(pattern = "yyyy-MM-dd") String startTransDate,
            @RequestParam(name = "endTransDate", required = false) @JsonFormat(pattern = "yyyy-MM-dd") String endTransDate,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "transDate") String soryBy,
            @RequestParam(name = "direction", defaultValue = "desc") String direction
    ) throws DocumentException {
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
        Page<Bill> transactions = transactionService.getDataAll(searchTransactionRequest);

        // Create Headers
        HttpHeaders headers = new HttpHeaders();
        // Set Content Headers
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "transaction.pdf");

        // Export data transaction to ByteArrayInputStream
        ByteArrayInputStream bis = exportFileService.exportTransactionToPdf(transactions);

        // Set Header Value
        String headerValue = String.format("attachment; filename=%s", headers.getContentDisposition().getFilename());

        // Response Entity
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
