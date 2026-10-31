package com.furnistore.service;

import com.furnistore.dto.CheckoutRequest;
import com.furnistore.dto.OrderItemResponse;
import com.furnistore.dto.OrderResponse;
import com.furnistore.entity.*;
import com.furnistore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CouponService couponService; // ✅ Added dependency

    public OrderService(OrderRepository orderRepository, 
                        CartRepository cartRepository,
                        AddressRepository addressRepository, 
                        UserRepository userRepository,
                        CouponService couponService) { // ✅ Added to constructor
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.couponService = couponService;
    }

    @Transactional
    public OrderResponse placeOrder(Long userId, CheckoutRequest request) {
        // 1. Get User and Address
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Address does not belong to this user");
        }

        // 2. Get Cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with an empty cart");
        }

        // 3. Create Order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus("PENDING");

        BigDecimal originalTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 4. Move Cart Items to Order Items & Calculate Original Total
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
            
            originalTotal = originalTotal.add(
                cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
            
            orderItems.add(orderItem);
        }

        // ✅ 5. Apply Coupon Discount
        BigDecimal discount = couponService.validateAndApplyCoupon(
            request.getCouponCode(), originalTotal
        );
        
        BigDecimal finalTotal = originalTotal.subtract(discount);

        order.setItems(orderItems);
        order.setTotalAmount(finalTotal); // ✅ Use discounted total

        // 6. Save Order and Clear Cart
        orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderHistory(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrdersForAdmin() {
        return orderRepository.findAllByOrderByOrderDateDesc().stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemResponse itemRes = new OrderItemResponse();
            itemRes.setProductName(item.getProduct().getName());
            
            List<String> images = item.getProduct().getImages();
            String imageUrl = (images != null && !images.isEmpty()) ? images.get(0) : null;
            itemRes.setProductImage(imageUrl);
            
            itemRes.setQuantity(item.getQuantity());
            itemRes.setPrice(item.getPriceAtPurchase());
            itemResponses.add(itemRes);
        }
        response.setItems(itemResponses);
        return response;
    }
}