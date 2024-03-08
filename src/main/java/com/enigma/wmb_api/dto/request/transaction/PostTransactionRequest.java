package com.enigma.wmb_api.dto.request.transaction;

import com.enigma.wmb_api.dto.request.transaction_detail.PostTransactionDetailRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostTransactionRequest {
    @NotNull(message = "user id is required")
    @NotBlank(message = "user id can't be blank")
    private String userId;

//    @NotNull(message = "table id is required")
//    @NotBlank(message = "table id can't be blank")
    private String tableId;

    @NotNull(message = "transaction detail is required")
    private List<PostTransactionDetailRequest> transactionDetails;
}
