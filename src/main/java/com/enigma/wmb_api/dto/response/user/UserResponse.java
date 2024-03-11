package com.enigma.wmb_api.dto.response.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String name;
    private String phoneNumber;
    private Boolean status;
    private UserAccountResponse userAccount;
}
