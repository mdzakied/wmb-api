package com.enigma.wmb_api.dto.response.transaction;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountTransactionResponse {
    String id;
    String username;
    List<RoleTransactionResponse> roles;
    Boolean isEnable;
}
