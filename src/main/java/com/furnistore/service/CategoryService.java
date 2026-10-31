package com.furnistore.service;

import com.furnistore.dto.CategoryRequest; // Ensure this DTO exists
import com.furnistore.entity.Category;
import com.furnistore.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> findAll() { 
        return repository.findAll(); 
    }

    public Category findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + id));
    }

    @Transactional
    public Category create(CategoryRequest request) {
        // Check if category name already exists (since ID is auto-generated)
        if (repository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists: " + request.getName());
        }
        
        Category category = new Category();
        category.setName(request.getName());
        // If you have an image field in Category, add: category.setImage(request.getImage());
        
        return repository.save(category);
    }

    @Transactional
    public Category update(Long id, CategoryRequest request) {
        Category category = findById(id);
        category.setName(request.getName());
        // If you have an image field: category.setImage(request.getImage());
        return repository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + id);
        }
        repository.deleteById(id);
    }
}