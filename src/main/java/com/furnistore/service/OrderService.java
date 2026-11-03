package com.furnistore.service;

import com.furnistore.dto.CheckoutRequest;  // ✅ CORRECT
import com.furnistore.entity.*;
import com.furnistore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, 
                        CartRepository cartRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public Order checkout(Long userId, CheckoutRequest request) { // ✅ MUST be CheckoutRequest
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // ✅ This will now correctly read the addressId from CheckoutRequest
        Address shippingAddress = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(shippingAddress); // Matches your entity
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        
        // Calculate total
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            BigDecimal itemTotal = cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(itemTotal);
        }
        order.setTotalAmount(total);

        // Save order first to get the ID
        order = orderRepository.save(order);

     // Create OrderItems
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            
            // 1. Link the Order
            orderItem.setOrder(order);
            
            // 2. ✅ FIX: Actually set the Product object!
            orderItem.setProduct(cartItem.getProduct());
            
            // 3. Set the Quantity
            orderItem.setQuantity(cartItem.getQuantity());
            
            // 4. Set the Price (extract the numeric price FROM the product)
            orderItem.setPrice(cartItem.getProduct().getPrice());
            
            // 5. Save snapshot of name/image in case product details change later
            orderItem.setProductName(cartItem.getProduct().getName());
            if (cartItem.getProduct().getImages() != null && !cartItem.getProduct().getImages().isEmpty()) {
                orderItem.setProductImage(cartItem.getProduct().getImages().get(0));
            }
            
            // Add to the order's list of items
            order.getItems().add(orderItem);
        }
        // Clear the cart after successful order
        cart.getItems().clear();
        cartRepository.save(cart);

        return order;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public Order getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        return order;
    }
    
    // ✅ NEW METHOD: Get all orders for the Admin Dashboard
    public List<Order> getAllOrdersForAdmin() {
        // This fetches all orders from the database, sorted by newest first
        return orderRepository.findAllByOrderByOrderDateDesc(); 
    }
}