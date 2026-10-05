package com.codewithmosh.store.payments;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CheckOutRequest {
    @NotNull(message = "Cart ID is required!")
    private UUID cartId;
}
