package com.codewithmosh.store.payments;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class WebhookRequest {
    Map<String, String> headers;
    String payload;
}
