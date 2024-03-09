package com.enigma.wmb_api.dto.response.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private String timestamp;
    private Integer statusCode;
    private String message;
}
