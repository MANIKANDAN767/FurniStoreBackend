package com.furnistore.service;

import com.furnistore.dto.AddToCartRequest;
import com.furnistore.dto.CartItemResponse;
import com.furnistore.dto.CartResponse;
import com.furnistore.dto.UpdateQuantityRequest;
import com.furnistore.entity.Cart;
import com.furnistore.entity.CartItem;
import com.furnistore.entity.Product;
import com.furnistore.entity.User;
import com.furnistore.repository.CartItemRepository;
import com.furnistore.repository.CartRepository;
import com.furnistore.repository.ProductRepository;
import com.furnistore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }
        return mapToCartResponse(cart);
    }

    public CartResponse viewCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            CartResponse empty = new CartResponse();
            empty.setCartId(0L);
            empty.setItems(new ArrayList<>());
            empty.setTotalItems(0);
            empty.setTotalPrice(BigDecimal.ZERO);
            return empty;
        }
        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse updateQuantity(Long userId, Long cartItemId, UpdateQuantityRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (request.getQuantity() <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        }
        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse removeFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return mapToCartResponse(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }

    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        List<CartItemResponse> itemResponses = new ArrayList<>();
        int totalItems = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            CartItemResponse itemRes = new CartItemResponse();
            itemRes.setId(item.getId());
            itemRes.setProductId(item.getProduct().getId());
            itemRes.setProductName(item.getProduct().getName());
            
            List<String> images = item.getProduct().getImages();
            String imageUrl = (images != null && !images.isEmpty()) ? images.get(0) : null;
            itemRes.setProductImage(imageUrl);
            
            itemRes.setPrice(item.getProduct().getPrice());
            itemRes.setQuantity(item.getQuantity());
            
            BigDecimal subtotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            itemRes.setSubtotal(subtotal);

            itemResponses.add(itemRes);
            totalItems += item.getQuantity();
            totalPrice = totalPrice.add(subtotal);
        }

        response.setItems(itemResponses);
        response.setTotalItems(totalItems);
        response.setTotalPrice(totalPrice);
        return response;
    }
}