package com.enigma.wmb_api.dto.response.transaction_detail;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetailResponse {
    private String id;
    private MenuTransactionDetailResponse menu;
    private Integer qty;
    private Integer price;
}
