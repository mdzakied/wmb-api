package com.enigma.wmb_api.dto.response.transaction;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransTypeTransactionResponse {
    private String id;
    private String desc;
}
