package com.codewithmosh.store.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "Quantity Must Be Provided")
    @Min(value = 1, message = "Quantity Must Be Greater than Zero")
    @Max(value = 1000, message = "Quantity Must Be Less than 1000")
    private Integer quantity;
}
