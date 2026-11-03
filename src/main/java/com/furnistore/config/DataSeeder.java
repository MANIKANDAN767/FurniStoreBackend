package com.furnistore.config;

import com.furnistore.entity.Category;
import com.furnistore.entity.Product;
import com.furnistore.entity.Role; // ✅ Import the Role enum
import com.furnistore.entity.User;
import com.furnistore.repository.CategoryRepository;
import com.furnistore.repository.ProductRepository;
import com.furnistore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ProductRepository productRepository, CategoryRepository categoryRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Users (Admin & Customer)
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@furnistore.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN); // ✅ Pass the Enum, not a String
            userRepository.save(admin);

            User customer = new User();
            customer.setName("John Customer");
            customer.setEmail("customer@furnistore.com");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setRole(Role.CUSTOMER); // ✅ Pass the Enum, not a String
            userRepository.save(customer);
            
            System.out.println("✅ Seeded Users (Admin & Customer)");
        }

        // 2. Seed Categories
        if (categoryRepository.count() == 0) {
            seedCategories();
        }
        
        // 3. Seed Products
        if (productRepository.count() == 0) {
            seedProducts();
        }
    }

    private void seedCategories() {
        Map<String, String> categoryImages = new HashMap<>();
        categoryImages.put("Sofas & Sectionals", "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?q=80&w=1000&auto=format&fit=crop");
        categoryImages.put("Chairs", "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?q=80&w=1000&auto=format&fit=crop");
        categoryImages.put("Tables", "https://images.unsplash.com/photo-1577140917170-285929fb55b7?q=80&w=1000&auto=format&fit=crop");
        categoryImages.put("Beds", "https://images.unsplash.com/photo-1713365963723-655fa464b681?auto=format&fit=crop&q=80&w=1000");
        categoryImages.put("Storage", "https://images.unsplash.com/photo-1595428774223-ef52624120d2?q=80&w=1000&auto=format&fit=crop");

        for (Map.Entry<String, String> entry : categoryImages.entrySet()) {
            Category cat = new Category();
            cat.setName(entry.getKey());
            cat.setImageUrl(entry.getValue()); 
            categoryRepository.save(cat);
        }
        System.out.println("✅ Seeded Categories");
    }

    private void seedProducts() {
        List<Category> categories = categoryRepository.findAll();
        Map<String, Category> catMap = new HashMap<>();
        for (Category c : categories) {
            catMap.put(c.getName(), c);
        }

        createProduct("prod_001", "SKU-001", "Modern Grey Sofa", catMap.get("Sofas & Sectionals"), "Fabric", "Grey", 499.99, 599.99, 4.5, 120, "New", "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?q=80&w=900&auto=format&fit=crop", "A comfortable modern sofa.", 50);
        createProduct("prod_002", "SKU-002", "Marlow Sectional", catMap.get("Sofas & Sectionals"), "Velvet", "Navy", 899.99, 1099.99, 4.8, 85, "Best Seller", "https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?q=80&w=900&auto=format&fit=crop", "Luxurious velvet sectional.", 20);
        createProduct("prod_003", "SKU-003", "Arlo Lounge Chair", catMap.get("Chairs"), "Leather", "Brown", 249.99, 299.99, 4.2, 45, "", "https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?q=80&w=900&auto=format&fit=crop", "Mid-century modern lounge chair.", 30);
        
        System.out.println("✅ Seeded Products");
    }

    private void createProduct(String id, String sku, String name, Category category, String material, String color, double price, double compareAt, double rating, int reviewCount, String tag, String imageUrl, String description, int stock) {
        if (category == null) return;
        Product product = new Product();
        product.setId(id);
        product.setSku(sku);
        product.setName(name);
        product.setCategory(category);
        product.setMaterial(material);
        product.setColor(color);
        product.setPrice(BigDecimal.valueOf(price));
        product.setCompareAt(BigDecimal.valueOf(compareAt));
        product.setRating(BigDecimal.valueOf(rating));
        product.setReviewCount(reviewCount);
        product.setTag(tag);
        product.setImages(Arrays.asList(imageUrl));
        product.setDescription(description);
        product.setSpecs(new HashMap<>());
        product.setStock(stock);
        productRepository.save(product);
    }
}