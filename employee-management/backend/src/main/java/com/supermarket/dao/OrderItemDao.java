package com.supermarket.dao;

import com.supermarket.entity.OrderItem;
import java.util.List;

/**
 * 订单项DAO接口
 */
public interface OrderItemDao {
    /**
     * 添加订单项
     * @param orderItem 订单项对象
     * @return 影响的行数
     */
    int add(OrderItem orderItem);
    
    /**
     * 批量添加订单项
     * @param orderItems 订单项列表
     * @return 影响的行数
     */
    int batchAdd(List<OrderItem> orderItems);
    
    /**
     * 更新订单项
     * @param orderItem 订单项对象
     * @return 影响的行数
     */
    int update(OrderItem orderItem);
    
    /**
     * 根据ID删除订单项
     * @param id 订单项ID
     * @return 影响的行数
     */
    int deleteById(int id);
    
    /**
     * 根据订单ID删除订单项
     * @param orderId 订单ID
     * @return 影响的行数
     */
    int deleteByOrderId(int orderId);
    
    /**
     * 根据ID查询订单项
     * @param id 订单项ID
     * @return 订单项对象
     */
    OrderItem findById(int id);
    
    /**
     * 根据订单ID查询订单项
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItem> findByOrderId(int orderId);
    
    /**
     * 根据订单编号查询订单项
     * @param orderNo 订单编号
     * @return 订单项列表
     */
    List<OrderItem> findByOrderNo(String orderNo);
} 