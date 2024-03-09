package com.enigma.wmb_api.dto.request.menu;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PutMenuRequest {
    @NotNull(message = "id is required")
    @NotBlank(message = "id can't be blank")
    private String id;

    @NotNull(message = "name is required")
    @NotBlank(message = "name can't be blank")
    private String name;

    @NotNull(message = "price is required")
    @Min(value = 1, message = "price must be greater than 1")
    private Integer price;

    private MultipartFile image;
}
