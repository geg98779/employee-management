package com.supermarket.service;

import com.supermarket.entity.Supplier;
import java.util.List;

public interface SupplierService {
    /**
     * 添加供应商
     * @param supplier 供应商对象
     * @return 影响的行数
     */
    int addSupplier(Supplier supplier);
    
    /**
     * 更新供应商
     * @param supplier 供应商对象
     * @return 影响的行数
     */
    int updateSupplier(Supplier supplier);
    
    /**
     * 删除供应商
     * @param id 供应商ID
     * @return 影响的行数
     */
    int deleteSupplier(int id);
    
    /**
     * 根据ID获取供应商
     * @param id 供应商ID
     * @return 供应商对象
     */
    Supplier getSupplierById(int id);
    
    /**
     * 获取所有供应商
     * @return 供应商列表
     */
    List<Supplier> getAllSuppliers();
} 