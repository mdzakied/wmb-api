package com.enigma.wmb_api.dto.request.menu;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchMenuRequest {
    // Query Params
    private String name;
    private Integer price;
    private Integer minPrice;
    private Integer maxPrice;
    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
