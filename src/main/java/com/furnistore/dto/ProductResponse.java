package com.furnistore.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductResponse(
        String id,
        String sku,
        String name,
        String category,
        String categoryName,
        String material,
        String color,
        BigDecimal price,
        BigDecimal compareAt,
        BigDecimal rating,
        Integer reviews,
        String tag,
        List<String> images,
        String description,
        Map<String, String> specs,
        Integer stock
) {}
