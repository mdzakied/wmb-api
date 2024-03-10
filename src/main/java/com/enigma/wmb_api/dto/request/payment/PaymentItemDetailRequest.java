package com.enigma.wmb_api.dto.request.payment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentItemDetailRequest {
    private String id;
    private String name;
    private Integer quantity;
    private Integer price;
}