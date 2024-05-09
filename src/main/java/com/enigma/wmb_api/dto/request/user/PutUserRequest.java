package com.enigma.wmb_api.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PutUserRequest {
    @NotNull(message = "id is required")
    @NotBlank(message = "id can't be blank")
    private String id;

    @NotNull(message = "name is required")
    @NotBlank(message = "name can't be blank")
    private String name;

    private String phoneNumber;

    @NotNull(message = "phone number is required")
    private Boolean status;
}
