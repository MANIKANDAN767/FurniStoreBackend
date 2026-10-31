package com.furnistore.config;

import com.furnistore.entity.Category;
import com.furnistore.entity.Product;
import com.furnistore.repository.CategoryRepository;
import com.furnistore.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
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

    public DataSeeder(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // ✅ SAFE: Only seed if the database is empty. 
        // This prevents foreign key constraint errors on restart.
        if (categoryRepository.count() == 0) {
            seedCategories();
        }
        
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
            // Note: Ensure your Category entity has an 'imageUrl' or 'image' field. 
            // If it's named 'image', change this to cat.setImage(entry.getValue());
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

        createProduct("prod_001", "SKU-001", "Modern Grey Sofa", catMap.get("Sofas & Sectionals"), 
            "Fabric", "Grey", 499.99, 599.99, 4.5, 120, "New", 
            "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?q=80&w=900&auto=format&fit=crop",
            "A comfortable modern sofa perfect for any living room.", 50);

        createProduct("prod_002", "SKU-002", "Marlow Sectional", catMap.get("Sofas & Sectionals"), 
            "Velvet", "Navy", 899.99, 1099.99, 4.8, 85, "Best Seller", 
            "https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?q=80&w=900&auto=format&fit=crop",
            "Luxurious velvet sectional with deep seating.", 20);

        createProduct("prod_003", "SKU-003", "Arlo Lounge Chair", catMap.get("Chairs"), 
            "Leather", "Brown", 249.99, 299.99, 4.2, 45, "", 
            "https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?q=80&w=900&auto=format&fit=crop",
            "Mid-century modern lounge chair in genuine leather.", 30);

        createProduct("prod_004", "SKU-004", "Haven Accent Chair", catMap.get("Chairs"), 
            "Fabric", "Mustard", 189.99, 229.99, 4.6, 60, "Sale", 
            "https://images.unsplash.com/photo-1580480055273-228ff5388ef8?q=80&w=900&auto=format&fit=crop",
            "Brighten up your corner with this stylish accent chair.", 40);

        createProduct("prod_005", "SKU-005", "Oak Dining Table", catMap.get("Tables"), 
            "Wood", "Natural", 599.99, 750.00, 4.7, 30, "", 
            "https://images.unsplash.com/photo-1577140917170-285929fb55b7?q=80&w=900&auto=format&fit=crop",
            "Solid oak dining table that seats 6 comfortably.", 15);

        createProduct("prod_006", "SKU-006", "Walnut Coffee Table", catMap.get("Tables"), 
            "Wood", "Dark Walnut", 149.99, 199.99, 4.3, 55, "", 
            "https://images.unsplash.com/photo-1532372320572-cda25653a26d?q=80&w=900&auto=format&fit=crop",
            "Minimalist walnut coffee table with storage shelf.", 25);

        createProduct("prod_007", "SKU-007", "Luna Platform Bed", catMap.get("Beds"), 
            "Wood/Fabric", "White", 799.99, 999.99, 4.9, 100, "Top Rated", 
            "https://images.unsplash.com/photo-1713365963723-655fa464b681?auto=format&fit=crop&q=80&w=900",
            "Modern platform bed with upholstered headboard.", 10);

        createProduct("prod_008", "SKU-008", "Oslo Sideboard", catMap.get("Storage"), 
            "Wood", "Light Oak", 449.99, 549.99, 4.4, 20, "", 
            "https://images.unsplash.com/photo-1595428774223-ef52624120d2?q=80&w=900&auto=format&fit=crop",
            "Scandinavian style sideboard with ample storage.", 12);

        createProduct("prod_009", "SKU-009", "Reed Storage Cabinet", catMap.get("Storage"), 
            "Metal/Wood", "Black", 129.99, 159.99, 4.1, 35, "", 
            "https://images.unsplash.com/photo-1595515106969-1ce29566ff1c?q=80&w=900&auto=format&fit=crop",
            "Industrial style cabinet perfect for entryways.", 18);

        createProduct("prod_010", "SKU-010", "Harper Sofa", catMap.get("Sofas & Sectionals"), 
            "Linen", "Beige", 649.99, 799.99, 4.5, 40, "", 
            "https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?q=80&w=900&auto=format&fit=crop",
            "Classic linen sofa with timeless design.", 22);
            
        System.out.println("✅ Seeded Products");
    }

    private void createProduct(String id, String sku, String name, Category category, 
                               String material, String color, double price, double compareAt, 
                               double rating, int reviewCount, String tag, String imageUrl, 
                               String description, int stock) {
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