package com.enigma.wmb_api.dto.request.table;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchTableRequest {
    // Query Params
    private String name;
    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
