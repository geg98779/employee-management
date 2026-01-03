package com.supermarket.dao;

import com.supermarket.entity.Supplier;
import java.util.List;

public interface SupplierDao {
    /**
     * 添加供应商
     * @param supplier 供应商对象
     * @return 影响的行数
     */
    int add(Supplier supplier);
    
    /**
     * 更新供应商
     * @param supplier 供应商对象
     * @return 影响的行数
     */
    int update(Supplier supplier);
    
    /**
     * 根据ID删除供应商
     * @param id 供应商ID
     * @return 影响的行数
     */
    int deleteById(int id);
    
    /**
     * 根据ID查询供应商
     * @param id 供应商ID
     * @return 供应商对象
     */
    Supplier findById(int id);
    
    /**
     * 查询所有供应商
     * @return 供应商列表
     */
    List<Supplier> findAll();
} 