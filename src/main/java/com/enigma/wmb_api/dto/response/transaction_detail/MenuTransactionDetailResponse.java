package com.enigma.wmb_api.dto.response.transaction_detail;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuTransactionDetailResponse {
    private String id;
    private String name;
    private Integer price;
}
