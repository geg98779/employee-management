package com.supermarket.dao;

import com.supermarket.entity.Order;
import java.util.List;

/**
 * 订单DAO接口
 */
public interface OrderDao {
    /**
     * 添加订单
     * @param order 订单对象
     * @return 生成的订单ID
     */
    int add(Order order);
    
    /**
     * 更新订单
     * @param order 订单对象
     * @return 影响的行数
     */
    int update(Order order);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 影响的行数
     */
    int updateStatus(int id, int status);
    
    /**
     * 根据ID删除订单
     * @param id 订单ID
     * @return 影响的行数
     */
    int deleteById(int id);
    
    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单对象
     */
    Order findById(int id);
    
    /**
     * 查询所有订单
     * @return 订单列表
     */
    List<Order> findAll();
    
    /**
     * 根据用户ID查询订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(int userId);
    
    /**
     * 根据订单编号查询订单
     * @param orderNo 订单编号
     * @return 订单对象
     */
    Order findByOrderNo(String orderNo);
} 