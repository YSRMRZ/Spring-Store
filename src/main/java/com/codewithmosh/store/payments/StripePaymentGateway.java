package com.codewithmosh.store.payments;

import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderItem;
import com.codewithmosh.store.entities.OrderStatus;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StripePaymentGateway implements PaymentGateway {

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @Override
    @Transactional
    public CheckoutSession createCheckoutSession(Order order) {
        try {

            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .putMetadata("order_id",order.getId().toString())
                    .setPaymentIntentData(
                            SessionCreateParams.PaymentIntentData.builder()
                                    .putMetadata("order_id", order.getId().toString())
                                    .build()
                    );

            order.getItems().forEach(item -> {
                var LineItem = createLineItem(item);
                builder.addLineItem(LineItem);
            });
            var session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());
        }
        catch (StripeException ex) {
            System.out.println(ex.getMessage());
            throw new PaymentException();
        }
        }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            var payload = request.getPayload();
            var signature = request.getHeaders().get("stripe-signature");
            var event = Webhook.constructEvent(
                    payload,
                    signature,
                    webhookSecretKey
            );

            System.out.println(event.getType());

            return switch (event.getType()) {
                //update order status (PAID)
                case "payment_intent.succeeded" ->
                Optional.of(new PaymentResult(extractOrderId(event),OrderStatus.PAID));
                //update order status (FAILED)
                case "payment_intent.payment_failed" ->
                    Optional.of(new PaymentResult(extractOrderId(event),OrderStatus.FAILED));

                default -> Optional.empty();
            };
//            return Optional.empty();

        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid Signature");
        }
    }

//    private Long extractOrderId(Event event) {
//        try {
//            var paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().deserializeUnsafe();
//            var orderId = paymentIntent.getMetadata().get("order_id");
//            if (orderId == null) {
//                throw new PaymentException("Missing order_id in PaymentIntent metadata");
//            }
//            return Long.valueOf(orderId);
//        } catch (EventDataObjectDeserializationException e) {
//            throw new PaymentException("Could not deserialize Stripe Event: " + e.getMessage());
//        }
//    }

    private Long extractOrderId(Event event) {
        var stripeObject = event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new PaymentException("Could not deserialize Stripe Event ,Check The SDK and API Versions."));
        var paymentIntent = (PaymentIntent) stripeObject;
        var orderId = paymentIntent.getMetadata().get("order_id");
        if (orderId == null) {
            throw new PaymentException("Missing order_id in PaymentIntent metadata");
        }
        return Long.valueOf(orderId);
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem item) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmountDecimal(
                        item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(
                        createProductData(item)
                ).build();
    }

    private  SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }
}
