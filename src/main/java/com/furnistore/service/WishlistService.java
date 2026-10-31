package com.furnistore.service;

import com.furnistore.dto.AddToWishlistRequest;
import com.furnistore.dto.WishlistItemResponse;
import com.furnistore.dto.WishlistResponse;
import com.furnistore.entity.Product;
import com.furnistore.entity.User;
import com.furnistore.entity.Wishlist;
import com.furnistore.entity.WishlistItem;
import com.furnistore.repository.ProductRepository;
import com.furnistore.repository.UserRepository;
import com.furnistore.repository.WishlistItemRepository;
import com.furnistore.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository,
                           WishlistItemRepository wishlistItemRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private Wishlist getOrCreateWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUser(user);
                    return wishlistRepository.save(newWishlist);
                });
    }

    @Transactional
    public WishlistResponse addToWishlist(Long userId, AddToWishlistRequest request) {
        Wishlist wishlist = getOrCreateWishlist(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), product.getId())) {
            throw new RuntimeException("Product already in wishlist");
        }

        WishlistItem newItem = new WishlistItem();
        newItem.setWishlist(wishlist);
        newItem.setProduct(product);
        wishlist.getItems().add(newItem);
        wishlistItemRepository.save(newItem);

        return mapToWishlistResponse(wishlist);
    }

    // ✅ FIX: Added @Transactional(readOnly = true) to keep session open while reading items
    @Transactional(readOnly = true)
    public WishlistResponse viewWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);
        if (wishlist == null) {
            WishlistResponse empty = new WishlistResponse();
            empty.setWishlistId(0L);
            empty.setItems(new ArrayList<>());
            empty.setTotalItems(0);
            return empty;
        }
        return mapToWishlistResponse(wishlist);
    }

    @Transactional
    public WishlistResponse removeFromWishlist(Long userId, String productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);

        WishlistItem item = wishlistItemRepository
                .findByWishlistIdAndProductId(wishlist.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Product not in wishlist"));

        wishlist.getItems().remove(item);
        wishlistItemRepository.delete(item);

        return mapToWishlistResponse(wishlist);
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(Long userId, String productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);
        if (wishlist == null) {
            return false;
        }
        return wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId);
    }

    @Transactional
    public void clearWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);
        if (wishlist != null) {
            wishlist.getItems().clear();
            wishlistRepository.save(wishlist);
        }
    }

    private WishlistResponse mapToWishlistResponse(Wishlist wishlist) {
        WishlistResponse response = new WishlistResponse();
        response.setWishlistId(wishlist.getId());

        List<WishlistItemResponse> itemResponses = new ArrayList<>();

        for (WishlistItem item : wishlist.getItems()) {
            WishlistItemResponse itemRes = new WishlistItemResponse();
            itemRes.setId(item.getId());
            itemRes.setProductId(item.getProduct().getId());
            itemRes.setProductName(item.getProduct().getName());

            List<String> images = item.getProduct().getImages();
            String imageUrl = (images != null && !images.isEmpty()) ? images.get(0) : null;
            itemRes.setProductImage(imageUrl);

            itemRes.setPrice(item.getProduct().getPrice());
            itemRes.setAddedAt(item.getAddedAt());

            itemResponses.add(itemRes);
        }

        response.setItems(itemResponses);
        response.setTotalItems(itemResponses.size());

        return response;
    }
}