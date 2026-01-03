package com.supermarket.dao;

import com.supermarket.entity.Product;
import java.util.List;

/**
 * 商品DAO接口
 */
public interface ProductDao {
    /**
     * 添加商品
     * @param product 商品对象
     * @return 影响的行数
     */
    int add(Product product);
    
    /**
     * 更新商品
     * @param product 商品对象
     * @return 影响的行数
     */
    int update(Product product);
    
    /**
     * 根据ID删除商品
     * @param id 商品ID
     * @return 影响的行数
     */
    int deleteById(int id);
    
    /**
     * 根据ID查询商品
     * @param id 商品ID
     * @return 商品对象
     */
    Product findById(int id);
    
    /**
     * 查询所有商品
     * @return 商品列表
     */
    List<Product> findAll();
    
    /**
     * 根据分类ID查询商品
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> findByCategoryId(int categoryId);
    
    /**
     * 更新商品库存
     * @param id 商品ID
     * @param stock 库存数量
     * @return 影响的行数
     */
    int updateStock(int id, int stock);
} 