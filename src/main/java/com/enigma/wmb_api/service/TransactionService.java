package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.transaction.PostTransactionRequest;
import com.enigma.wmb_api.dto.request.transaction.SearchTransactionRequest;
import com.enigma.wmb_api.dto.response.transaction.TransactionResponse;
import org.springframework.data.domain.Page;

public interface TransactionService {
    TransactionResponse create(PostTransactionRequest postTransactionRequest);
    Page<TransactionResponse> getAll(SearchTransactionRequest searchTransactionRequest);
    TransactionResponse getOneById(String id);
}
