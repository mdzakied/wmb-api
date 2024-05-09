package com.enigma.wmb_api.dto.response.auth;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String id;
    private String username;
    private String token;
    private List<String> roles;
    private Boolean isEnabled;
}
