package com.enigma.wmb_api.dto.response.transaction_detail;

import com.enigma.wmb_api.dto.response.ImageResponse;
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
    private ImageResponse image;
}
