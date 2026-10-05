package com.codewithmosh.store.DTO;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class OrderItemDto {
    private OrderProductDto product;
    private int quantity;
    private BigDecimal totalPrice;


}
