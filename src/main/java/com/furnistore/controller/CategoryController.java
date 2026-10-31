package com.furnistore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.furnistore.dto.CategoryRequest;
import com.furnistore.entity.Category;
import com.furnistore.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "${furnistore.frontend-url}")
public class CategoryController {
    
    private final CategoryService service;

    public CategoryController(CategoryService service) { 
        this.service = service; 
    }

    @GetMapping
    public List<Category> getAll() { 
        return service.findAll(); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id)); // Fixed: 'service' instead of 'categoryService'
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); // Fixed: 'service'
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(
            @PathVariable Long id,  // Fixed: String -> Long
            @Valid @RequestBody CategoryRequest request) { 
        return ResponseEntity.ok(service.update(id, request)); // Fixed: 'service' + Long id
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { // Fixed: String -> Long
        service.delete(id); // Fixed: 'service'
    }
}