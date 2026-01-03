package com.supermarket.service;

import com.supermarket.entity.Product;
import java.util.List;

public interface ProductService {
    /**
     * 添加商品
     * @param product 商品对象
     * @return 影响的行数
     */
    int addProduct(Product product);
    
    /**
     * 更新商品
     * @param product 商品对象
     * @return 影响的行数
     */
    int updateProduct(Product product);
    
    /**
     * 删除商品
     * @param id 商品ID
     * @return 影响的行数
     */
    int deleteProduct(int id);
    
    /**
     * 根据ID获取商品
     * @param id 商品ID
     * @return 商品对象
     */
    Product getProductById(int id);
    
    /**
     * 获取所有商品
     * @return 商品列表
     */
    List<Product> getAllProducts();
    
    /**
     * 根据分类ID获取商品
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> getProductsByCategoryId(int categoryId);
    
    /**
     * 更新商品库存
     * @param id 商品ID
     * @param stock 库存数量
     * @return 影响的行数
     */
    int updateProductStock(int id, int stock);
} 