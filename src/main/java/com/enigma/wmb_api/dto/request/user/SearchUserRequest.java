package com.enigma.wmb_api.dto.request.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserRequest {
    // Query Params
    private String name;
    private String phoneNumber;
    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
