package com.furnistore.service;

import com.furnistore.entity.Order;
import com.furnistore.entity.Payment;
import com.furnistore.repository.OrderRepository;
import com.furnistore.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String apiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    /**
     * Creates a Stripe Payment Intent for an existing order.
     * Returns the client_secret for frontend to complete payment.
     */
    @Transactional
    public Map<String, Object> createPaymentIntent(Long orderId) throws StripeException {
        // ✅ Validate API key before calling Stripe
        if (apiKey == null || !apiKey.startsWith("sk_test_")) {
            throw new RuntimeException("Invalid Stripe API key. Check application.properties");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Only PENDING orders can be paid. Current status: " + order.getStatus());
        }

        Payment existing = paymentRepository.findByOrderId(orderId).orElse(null);
        if (existing != null && existing.getStatus() == Payment.PaymentStatus.SUCCEEDED) {
            throw new RuntimeException("Order already paid");
        }

        long amountInCents = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amountInCents);
        params.put("currency", "usd");
        params.put("metadata", Map.of("orderId", String.valueOf(orderId)));

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            
            Payment payment = existing != null ? existing : new Payment();
            payment.setOrder(order);
            payment.setStripePaymentIntentId(intent.getId());
            payment.setAmount(order.getTotalAmount());
            payment.setStatus(Payment.PaymentStatus.PENDING);
            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", intent.getClientSecret());
            response.put("paymentIntentId", intent.getId());
            return response;
            
        } catch (StripeException e) {
            // ✅ Log the ACTUAL Stripe error instead of generic 500
            throw new RuntimeException("Stripe API Error: " + e.getMessage(), e);
        }
    }
    /**
     * Processes Stripe Webhook events.
     * THIS IS THE SOURCE OF TRUTH FOR PAYMENT STATUS.
     */
    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) throws Exception {
        Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                String orderIdStr = intent.getMetadata().get("orderId");
                if (orderIdStr != null) {
                    Long orderId = Long.parseLong(orderIdStr);
                    Order order = orderRepository.findById(orderId)
                            .orElseThrow(() -> new RuntimeException("Order not found"));
                    order.setStatus("CONFIRMED");
                    orderRepository.save(order);

                    Payment payment = paymentRepository
                            .findByStripePaymentIntentId(intent.getId()).orElse(null);
                    if (payment != null) {
                        payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
                        payment.setPaidAt(LocalDateTime.now());
                        paymentRepository.save(payment);
                    }
                }
                break;

            case "payment_intent.payment_failed":
                // Handle failed payments similarly
                break;
        }
    }
}