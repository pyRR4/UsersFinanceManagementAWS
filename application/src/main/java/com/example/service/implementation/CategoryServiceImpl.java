package com.example.service.implementation;

import com.example.exception.ResourceNotFoundException;
import com.example.model.Category;
import com.example.repository.contract.CategoryRepository;
import com.example.service.contract.CategoryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public void create(String name, int userId) {
        categoryRepository.findByNameAndUserId(name, userId)
                .ifPresent(c -> {
            throw new IllegalArgumentException("Category with this name already exists.");
        });

        categoryRepository.create(name, userId);
    }

    @Override
    public List<Category> findAllByUserId(int userId) {
        return categoryRepository.findAllByUserId(userId);
    }

    @Override
    public void update(int categoryId, String name, int userId) {
        categoryRepository.findByNameAndUserId(name, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id: " + categoryId
                        + " not found for user with id: " + userId));

        categoryRepository.update(categoryId, name, userId);
    }

    @Override
    public void deleteByIdAndUserId(int id, int userId) {
        categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id: " + id
                        + " not found for user with id: " + userId));

        categoryRepository.deleteByIdAndUserId(id, userId);
    }
}
