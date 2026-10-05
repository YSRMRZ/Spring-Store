package com.codewithmosh.store.payments;

import com.codewithmosh.store.entities.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResult {
    private Long orderId;
    private OrderStatus paymentStatus;
}
