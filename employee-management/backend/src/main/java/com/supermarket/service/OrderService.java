package com.supermarket.service;

import com.supermarket.entity.Order;
import java.util.List;
import java.util.Map;

public interface OrderService {
    /**
     * 创建订单
     * @param order 订单对象
     * @return 结果Map，包含success、message和data
     */
    Map<String, Object> createOrder(Order order);
    
    /**
     * 更新订单
     * @param order 订单对象
     * @return 影响的行数
     */
    int updateOrder(Order order);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 影响的行数
     */
    int updateOrderStatus(int id, int status);
    
    /**
     * 删除订单
     * @param id 订单ID
     * @return 影响的行数
     */
    int deleteOrder(int id);
    
    /**
     * 根据ID获取订单
     * @param id 订单ID
     * @return 订单对象
     */
    Order getOrderById(int id);
    
    /**
     * 获取所有订单
     * @return 订单列表
     */
    List<Order> getAllOrders();
    
    /**
     * 根据用户ID获取订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> getOrdersByUserId(int userId);
} 