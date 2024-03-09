package com.enigma.wmb_api.dto.response.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponsePage<T> {
    private Integer statusCode;
    private String message;
    private T data;
    private PagingResponse paging;
}
