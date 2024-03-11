package com.enigma.wmb_api.dto.response.user;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountResponse {
    String id;
    String username;
    List<RoleResponse> roles;
    Boolean isEnable;
}
