package com.enigma.wmb_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @NotNull(message = "username is required")
    @NotBlank(message = "username can't be blank")
    private String username;

    @NotNull(message = "password is required")
    @NotBlank(message = "password can't be blank")
    private String password;
}
