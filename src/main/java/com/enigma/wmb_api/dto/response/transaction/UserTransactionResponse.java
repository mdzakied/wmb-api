package com.enigma.wmb_api.dto.response.transaction;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTransactionResponse {
    private String id;
    private String name;
    private String phoneNumber;
    private Boolean status;
    private UserAccountTransactionResponse userAccount;
}
