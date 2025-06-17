package com.example.repository.contract;

import com.example.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    void create(String name, int userId);
    List<Category> findAllByUserId(int userId);
    void update(int categoryId, String name, int userId);
    void deleteByIdAndUserId(int id, int userId);
    Optional<Category> findByNameAndUserId(String name, int userId);
    Optional<Category> findByIdAndUserId(int categoryId, int userId);
}
