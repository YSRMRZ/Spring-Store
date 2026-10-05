package com.codewithmosh.store.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CartDto {
    private UUID id;
    private List<CartItemDto> items =new ArrayList<>();
    private BigDecimal totalPrice =new BigDecimal(0);

}
