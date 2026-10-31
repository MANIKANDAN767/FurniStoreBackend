package com.furnistore.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank String id,
        @NotBlank String sku,
        @NotBlank String name,
        @NotBlank String categoryId,
        String material,
        String color,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @DecimalMin("0.0") BigDecimal compareAt,
        BigDecimal rating,
        Integer reviews,
        String tag,
        List<String> images,
        String description,
        Map<String, String> specs,
        @NotNull Integer stock
) {}
