package com.enigma.wmb_api.dto.request.table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PutTableRequest {
    @NotNull(message = "id is required")
    @NotBlank(message = "id can't be blank")
    private String id;

    @NotNull(message = "name is required")
    @NotBlank(message = "name can't be blank")
    private String name;
}
