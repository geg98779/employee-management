package com.supermarket.service.impl;

import com.supermarket.dao.ProductDao;
import com.supermarket.dao.impl.ProductDaoImpl;
import com.supermarket.entity.Product;
import com.supermarket.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductDao productDao = new ProductDaoImpl();
    
    @Override
    public int addProduct(Product product) {
        return productDao.add(product);
    }
    
    @Override
    public int updateProduct(Product product) {
        return productDao.update(product);
    }
    
    @Override
    public int deleteProduct(int id) {
        return productDao.deleteById(id);
    }
    
    @Override
    public Product getProductById(int id) {
        return productDao.findById(id);
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }
    
    @Override
    public List<Product> getProductsByCategoryId(int categoryId) {
        return productDao.findByCategoryId(categoryId);
    }
    
    @Override
    public int updateProductStock(int id, int stock) {
        return productDao.updateStock(id, stock);
    }
} 