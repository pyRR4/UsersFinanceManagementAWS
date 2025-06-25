package com.example.service.contract;

import com.example.model.Category;

import java.util.List;

public interface CategoryService {
    void create(String name, int userId);
    List<Category> findAllByUserId(int userId);
    void update(int categoryId, String name, int userId);
    void deleteByIdAndUserId(int id, int userId);
}
