package com.enigma.wmb_api.dto.response.transaction;

import com.enigma.wmb_api.dto.response.PaymentResponse;
import com.enigma.wmb_api.dto.response.transaction_detail.TransactionDetailResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date transDate;
    private UserTransactionResponse user;
    private TableTransactionResponse table;
    private TransTypeTransactionResponse transType;
    private List<TransactionDetailResponse> transactionDetails;
    private PaymentResponse payment;
}
