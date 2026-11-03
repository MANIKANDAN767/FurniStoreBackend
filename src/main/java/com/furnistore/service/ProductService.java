package com.furnistore.service;

import com.furnistore.entity.Product;
import com.furnistore.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Query the actual MySQL database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Query the actual MySQL database by ID
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
}