package com.enigma.wmb_api.dto.request.table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostTableRequest {
    @NotNull(message = "name is required")
    @NotBlank(message = "name can't be blank")
    private String name;
}
