package com.enigma.wmb_api.dto.request.transaction;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchTransactionRequest {
    // Query Params
    private String userName;
    private String menuName;
    private String transDate;
    private String startTransDate;
    private String endTransDate;
    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
