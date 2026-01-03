package com.supermarket.dao;

import com.supermarket.entity.Category;
import java.util.List;

public interface CategoryDao {
    /**
     * 添加分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int add(Category category);
    
    /**
     * 更新分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int update(Category category);
    
    /**
     * 根据ID删除分类
     * @param id 分类ID
     * @return 影响的行数
     */
    int deleteById(Integer id);
    
    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类对象
     */
    Category findById(Integer id);
    
    /**
     * 查询所有分类
     * @return 分类列表
     */
    List<Category> findAll();
    
    /**
     * 根据父ID查询子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> findByParentId(Integer parentId);
} 