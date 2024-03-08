package com.enigma.wmb_api.dto.request.transaction_detail;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostTransactionDetailRequest {
    @NotNull(message = "menu id is required")
    @NotBlank(message = "menu id can't be blank")
    private String menuId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be greater than 0")
    private Integer qty;
}
