package com.supermarket.service.impl;

import com.supermarket.dao.CategoryDao;
import com.supermarket.dao.impl.CategoryDaoImpl;
import com.supermarket.entity.Category;
import com.supermarket.service.CategoryService;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao = new CategoryDaoImpl();
    
    @Override
    public int addCategory(Category category) {
        return categoryDao.add(category);
    }
    
    @Override
    public int updateCategory(Category category) {
        return categoryDao.update(category);
    }
    
    @Override
    public int deleteCategory(Integer id) {
        if (id == null) {
            return 0;
        }
        return categoryDao.deleteById(id);
    }
    
    @Override
    public Category getCategoryById(Integer id) {
        if (id == null) {
            return null;
        }
        return categoryDao.findById(id);
    }
    
    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }
    
    @Override
    public List<Category> getCategoriesByParentId(Integer parentId) {
        if (parentId == null) {
            parentId = 0;
        }
        return categoryDao.findByParentId(parentId);
    }
} 