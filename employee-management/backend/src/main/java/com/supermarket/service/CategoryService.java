package com.supermarket.service;

import com.supermarket.entity.Category;
import java.util.List;

public interface CategoryService {
    /**
     * 添加分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int addCategory(Category category);
    
    /**
     * 更新分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int updateCategory(Category category);
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响的行数
     */
    int deleteCategory(Integer id);
    
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象
     */
    Category getCategoryById(Integer id);
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 根据父ID获取子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> getCategoriesByParentId(Integer parentId);
} 