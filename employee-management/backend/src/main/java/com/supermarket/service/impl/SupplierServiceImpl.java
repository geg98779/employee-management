package com.supermarket.service.impl;

import com.supermarket.dao.SupplierDao;
import com.supermarket.dao.impl.SupplierDaoImpl;
import com.supermarket.entity.Supplier;
import com.supermarket.service.SupplierService;

import java.util.List;

public class SupplierServiceImpl implements SupplierService {
    private SupplierDao supplierDao = new SupplierDaoImpl();
    
    @Override
    public int addSupplier(Supplier supplier) {
        // 验证必填字段
        if (supplier == null || supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            System.out.println("添加供应商失败: 供应商名称为空");
            return 0;
        }
        
        // 设置默认值
        if (supplier.getStatus() != 0 && supplier.getStatus() != 1) {
            supplier.setStatus(1);  // 默认状态为正常
        }
        
        return supplierDao.add(supplier);
    }
    
    @Override
    public int updateSupplier(Supplier supplier) {
        return supplierDao.update(supplier);
    }
    
    @Override
    public int deleteSupplier(int id) {
        return supplierDao.deleteById(id);
    }
    
    @Override
    public Supplier getSupplierById(int id) {
        return supplierDao.findById(id);
    }
    
    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierDao.findAll();
    }
} 