package com.codewithmosh.store.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddItemToCartRequest {
    @NotBlank(message = "Product is required !")
    private Long productId;
}
